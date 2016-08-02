package ca.sfu.teambeta.api;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class UserAPITests extends APITest {
    @Test
    public void loginTest() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = login(EMAIL, PASSWORD, REMEMBER_ME);

        assertEquals(200, jsonResponse.getStatus());
        JsonNode node = jsonResponse.getBody();
        assertNotNull(node.getObject().get("sessionToken"));
    }

    @Test(expected = JSONException.class)
    public void loginFailureTest() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = login(EMAIL, "silly_me", REMEMBER_ME);
        assertEquals(401, jsonResponse.getStatus());
        JsonNode node = jsonResponse.getBody();
        // The sessionToken property shouldn't exists and should throw a JSONException
        node.getObject().get("sessionToken");
    }
}
