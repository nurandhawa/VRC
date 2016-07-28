package ca.sfu.teambeta.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by gordo on 7/27/2016.
 */
public class MatchesAPITest extends APITest {
    @Test
    public void testGetLatestMatchesEmptyLoggedIn() throws UnirestException {
        login(EMAIL, PASSWORD);
        HttpResponse<JsonNode> jsonResponse = Unirest.get(URI_BASENAME + "api/matches")
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

        HttpResponse<JsonNode> jsonResponse = Unirest.get(URI_BASENAME + "api/matches")
                .queryString("gameSession", "latest")
                .header("accept", "application/json")
                .asJson();

        assertEquals(200, jsonResponse.getStatus());

        JsonNode node = jsonResponse.getBody();
        assertEquals(3, node.getArray().length());
    }
}
