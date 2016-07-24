package ca.sfu.teambeta.api;


import com.google.gson.Gson;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.hibernate.SessionFactory;
import org.json.JSONException;
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

/**
 * Created by Gordon Shieh on 23/07/16.
 */
public class APITest {
    public static final String EMAIL = "testuser@vrc.com";
    public static final String PASSWORD = "demoPass";
    private static String HOSTNAME = "https://localhost:8000/";

    @Before
    public void startServer() throws Exception {
        Runnable runnable = () -> {
            DBManager dbManager;
            try {
                Ladder newLadder = null;
                try {
                    newLadder = CSVReader.setupTestingLadder();
                } catch (Exception e) {
                    System.out.println("INVALID CSV FILE");
                    throw e;
                }
                SessionFactory sessionFactory = DBManager.getMySQLSession(true);

                GameSession gameSession = new GameSession(newLadder);
                gameSession.setUpLastWeekPositions();
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
                .build();
        Unirest.setHttpClient(httpclient);
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

}
