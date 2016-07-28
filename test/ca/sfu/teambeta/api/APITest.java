package ca.sfu.teambeta.api;


import com.google.gson.Gson;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.hibernate.SessionFactory;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import ca.sfu.teambeta.AppController;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.logic.AccountManager;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

/**
 * API testing using the Unirest library.
 * Every test will spawn a fresh SparkJava server, and will use an In-Memory database for performance reasons
 * All Unirest calls will throw the "UnirestException" and should not be caught in the tests.
 * Under the hood, Unirest uses Apache's HTTP Libraries which support the use of cookies,
 * and require NO calls in the testing code involving cookies
 */
public class APITest {
    public static final String EMAIL = "testuser@vrc.com";
    public static final String PASSWORD = "demoPass";
    private static String HOSTNAME = "https://localhost:8000/";
    private int ladderLength;

    @Before
    public void startServer() throws Exception {
        Runnable runnable = () -> {
            DBManager dbManager;
            try {
                Ladder newLadder = null;
                try {
                    newLadder = CSVReader.setupTestingLadder();
                    ladderLength = newLadder.getLadderLength();
                } catch (Exception e) {
                    System.out.println("INVALID CSV FILE");
                    throw e;
                }
                SessionFactory sessionFactory = DBManager.getTestingSession(true);

                GameSession gameSession = new GameSession(newLadder);
                dbManager = new DBManager(sessionFactory);
                dbManager.persistEntity(gameSession);

                AccountManager am = new AccountManager(dbManager);
                am.register(EMAIL, PASSWORD);

                AppController appController =
                        new AppController(dbManager, AppController.DEVELOP_SERVER_PORT,
                                AppController.DEVELOP_STATIC_HTML_PATH);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail();
            }
        };
        new Thread(runnable).start();
        awaitInitialization();
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultCookieStore(new BasicCookieStore())
                .build();
        Unirest.setHttpClient(httpclient);
    }

    @After
    public void stopServer() {
        stop();
    }

    private HttpResponse<JsonNode> login(String email, String password) throws UnirestException {
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("email", email);
        loginParams.put("password", password);

        Gson gson = new Gson();

        return Unirest.post(HOSTNAME + "api/login")
                .header("accept", "application/json")
                .body(gson.toJson(loginParams))
                .asJson();
    }

    private HttpResponse<JsonNode> changePairToPlaying(int pairId) throws UnirestException {
        return Unirest.patch(HOSTNAME + "api/ladder/" + pairId)
                .queryString("newStatus", "playing")
                .asJson();
    }

    private HttpResponse<JsonNode> changePairToNotPlaying(int pairId) throws UnirestException {
        return Unirest.patch(HOSTNAME + "api/ladder/1")
                .queryString("newStatus", "not playing")
                .asJson();
    }

    @Test
    public void loginTest() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = login(EMAIL, PASSWORD);

        assertEquals(200, jsonResponse.getStatus());
        JsonNode node = jsonResponse.getBody();
        assertNotNull(node.getObject().get("sessionToken"));
    }

    @Test(expected = JSONException.class)
    public void loginFailureTest() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = login(EMAIL, "");

        assertEquals(401, jsonResponse.getStatus());
        JsonNode node = jsonResponse.getBody();
        // The sessionToken property shouldn't exists and should throw a JSONException
        node.getObject().get("sessionToken");
    }

    @Test
    public void testGetLadderLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD);
        HttpResponse<JsonNode> jsonResponse = Unirest.get(HOSTNAME + "api/ladder")
                .header("accept", "application/json")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());
        JsonNode node = jsonResponse.getBody();
        assertEquals(ladderLength, node.getArray().length());
    }

    @Test
    public void testGetLadderNotLoggedIn() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(HOSTNAME + "api/ladder")
                .header("accept", "application/json")
                .asJson();

        assertEquals(401, jsonResponse.getStatus());
    }

    @Test
    public void testChangePlayingStatusLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD);

        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(HOSTNAME + "api/ladder/1")
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(200, jsonPairUpdateResponse.getStatus());
    }

    @Test
    public void testChangePlayingStatusTwiceLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD);

        changePairToPlaying(1);

        HttpResponse<JsonNode> jsonPairUpdateResponse = changePairToPlaying(1);

        assertEquals(404, jsonPairUpdateResponse.getStatus());
    }

    @Test
    public void testChangePlayingStatusNotLoggedIn() throws UnirestException {
        HttpResponse<JsonNode> jsonPairUpdateResponse = changePairToPlaying(1);

        assertEquals(401, jsonPairUpdateResponse.getStatus());
    }

    @Test
    public void testGetLatestMatchesEmptyLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD);
        HttpResponse<JsonNode> jsonResponse = Unirest.get(HOSTNAME + "api/matches")
                .queryString("gameSession", "latest")
                .header("accept", "application/json")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());
    }

    @Test
    public void testGetLatestMatchesLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD);
        for (int i = 1; i <= 10; i++) {
            changePairToPlaying(i);
        }

        HttpResponse<JsonNode> jsonResponse = Unirest.get(HOSTNAME + "api/matches")
                .queryString("gameSession", "latest")
                .header("accept", "application/json")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());

        JsonNode node = jsonResponse.getBody();
        assertEquals(3, node.getArray().length());
    }
}
