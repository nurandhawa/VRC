package ca.sfu.teambeta.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class LadderAPITest extends APITest {

    @Test
    public void testGetLadderLoggedIn() throws UnirestException {
        super.login(EMAIL, PASSWORD, REMEMBER_ME);
        HttpResponse<JsonNode> jsonResponse = Unirest.get(URI_BASENAME + "api/ladder")
                .header("accept", "application/json")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());
        JsonNode node = jsonResponse.getBody();
        JSONArray ladder = node.getObject().getJSONArray("pairs");
        assertEquals(getOriginalLadderLength(), ladder.length());
    }

    @Test
    public void testGetLadderNotLoggedIn() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(URI_BASENAME + "api/ladder")
                .header("accept", "application/json")
                .asJson();

        assertEquals(401, jsonResponse.getStatus());
    }

    @Test
    public void testChangePlayingStatusLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD, REMEMBER_ME);

        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(URI_BASENAME + "api/ladder/1")
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(200, jsonPairUpdateResponse.getStatus());
    }

    @Test
    public void testChangePlayingStatusTwiceLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD, REMEMBER_ME);

        Unirest.patch(URI_BASENAME + "api/ladder/" + 1)
                .queryString("newStatus", "playing")
                .asJson();

        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(URI_BASENAME + "api/ladder/" + 1)
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(404, jsonPairUpdateResponse.getStatus());
    }

    @Test
    public void testChangePlayingStatusNotLoggedIn() throws UnirestException {
        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(URI_BASENAME + "api/ladder/" + 1)
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(401, jsonPairUpdateResponse.getStatus());
    }


    @Test
    public void testDeletePair() throws UnirestException {
        login(EMAIL, PASSWORD, REMEMBER_ME);
        Unirest.delete(URI_BASENAME + "api/ladder/" + 1)
                .asJson();

        HttpResponse<JsonNode> jsonResponse = Unirest.get(URI_BASENAME + "api/ladder")
                .header("accept", "application/json")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());
        JsonNode node = jsonResponse.getBody();
        JSONArray ladder = node.getObject().getJSONArray("pairs");
        assertEquals(getOriginalLadderLength() - 1, ladder.length());
    }

    @Test
    public void testDeletePairThenActive() throws UnirestException {
        login(EMAIL, PASSWORD, REMEMBER_ME);
        Unirest.delete(URI_BASENAME + "api/ladder/" + 1)
                .asJson();

        Unirest.get(URI_BASENAME + "api/ladder")
                .header("accept", "application/json")
                .asJson();

        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(URI_BASENAME + "api/ladder/" + 1)
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(500, jsonPairUpdateResponse.getStatus());
    }

    @Test
    public void testSetSamePlayerActive() throws UnirestException {
        login(EMAIL, PASSWORD, REMEMBER_ME);
        Unirest.patch(URI_BASENAME + "api/ladder/" + 2)
                .queryString("newStatus", "playing")
                .asJson();

        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(URI_BASENAME + "api/ladder/" + 17)
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(404, jsonPairUpdateResponse.getStatus());
    }

    @Test
    public void testDeletePairThenSamePlayerActive() throws UnirestException {
        login(EMAIL, PASSWORD, REMEMBER_ME);
        Unirest.delete(URI_BASENAME + "api/ladder/" + 2)
                .asJson();

        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(URI_BASENAME + "api/ladder/" + 17)
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(200, jsonPairUpdateResponse.getStatus());
    }

    @Test
    @Ignore
    public void testDeleteActivePairThenSamePlayerActive() throws UnirestException {
        login(EMAIL, PASSWORD, REMEMBER_ME);

        Unirest.patch(URI_BASENAME + "api/ladder/" + 2)
                .queryString("newStatus", "playing")
                .asJson();

        Unirest.delete(URI_BASENAME + "api/ladder/" + 2)
                .asJson();

        HttpResponse<JsonNode> jsonPairUpdateResponse = Unirest.patch(URI_BASENAME + "api/ladder/" + 17)
                .queryString("newStatus", "playing")
                .asJson();

        assertEquals(200, jsonPairUpdateResponse.getStatus());
    }
}
