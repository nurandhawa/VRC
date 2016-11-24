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
import org.junit.After;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import ca.sfu.teambeta.AppController;
import ca.sfu.teambeta.accounts.AccountDatabaseHandler;
import ca.sfu.teambeta.accounts.AccountManager;
import ca.sfu.teambeta.accounts.CredentialsManager;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

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
    public static final String EMAIL = "admin_billy@vrc.ca";
    public static final String PASSWORD = "demoPass";
    public static final String REMEMBER_ME = "false";
    public static final String URI_BASENAME = "http://localhost:8000/";
    public static final String SECURITY_QUESTION = "question";
    public static final String SECURITY_ANSWER = "answer";
    private int originalLadderLength;

    @Before
    public void startServer() throws Exception {
        Runnable runnable = () -> {
            DBManager dbManager;
            try {
                Ladder newLadder = null;
                SessionFactory sessionFactory = DBManager.getTestingSession(true);
                dbManager = new DBManager(sessionFactory);
                try {
                    newLadder = CSVReader.setupTestingLadder(dbManager);
                    originalLadderLength = newLadder.getLadderLength();
                } catch (Exception e) {
                    System.out.println("INVALID CSV FILE");
                    throw e;
                }
                GameSession gameSession = new GameSession(newLadder);
                dbManager.persistEntity(gameSession);

                AccountDatabaseHandler accountDbHandler = new AccountDatabaseHandler(dbManager);
                AccountManager am = new AccountManager(accountDbHandler);
                am.registerNewAdministratorAccount(EMAIL, PASSWORD, SECURITY_QUESTION, SECURITY_ANSWER);

                CredentialsManager cm = new CredentialsManager(accountDbHandler);

                new AppController(dbManager, cm, AppController.DEVELOP_SERVER_PORT,
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

    protected HttpResponse<JsonNode> login(String email, String password, String rememberMe) throws UnirestException {
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("email", email);
        loginParams.put("password", password);
        loginParams.put("rememberMe", rememberMe);

        Gson gson = new Gson();

        return Unirest.post(URI_BASENAME + "api/login")
                .header("accept", "application/json")
                .body(gson.toJson(loginParams))
                .asJson();
    }

    int getOriginalLadderLength() {
        return originalLadderLength;
    }

}
