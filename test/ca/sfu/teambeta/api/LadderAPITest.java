package ca.sfu.teambeta.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
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
        assertEquals(getLadderLength(), ladder.length());
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
}
