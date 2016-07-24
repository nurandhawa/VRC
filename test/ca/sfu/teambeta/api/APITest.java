package ca.sfu.teambeta.api;


import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import ca.sfu.teambeta.AppController;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.logic.AccountManager;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
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
    public void startServer() throws InterruptedException {
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
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:8000/";
    }

    @Test
    public void loginTest() {
        given()
                .body("{\"email\": \"" + EMAIL +
                        "\", \"password\": \"" + PASSWORD + "\" }")
                .when()
                .post("api/login")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void loginFailureTest() {
        given()
                .body("{\"email\": \"" + EMAIL +
                        "\", \"password\": \"" + "." + "\" }")
                .when()
                .post("api/login")
                .then()
                .assertThat()
                .statusCode(401);
    }

    // TODO: Make RestAssured work with checking for Json properties
}
