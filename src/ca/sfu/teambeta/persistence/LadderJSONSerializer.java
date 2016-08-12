package ca.sfu.teambeta.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Time;
import ca.sfu.teambeta.logic.GameSession;

/**
 * The Type of the Serializer is of GameSession because it requires other info from it in order to
 * properly serialize the Ladder to JSON
 */
public class LadderJSONSerializer implements JsonSerializer<GameSession> {

    private JsonObject getPairJsonObject(Pair pair, Map<Pair, Time> timeSlots,
                                         int position, boolean isPlaying) {
        JsonObject pairJson = new JsonObject();
        JsonArray playersArray = new JsonArray();
        for (Player player : pair.getPlayers()) {
            JsonObject playerJson = new JsonObject();
            playerJson.addProperty("firstName", player.getFirstName());
            playerJson.addProperty("lastName", player.getLastName());
            playerJson.addProperty("id", player.getID());
            playersArray.add(playerJson);
        }
        pairJson.add("players", playersArray);
        pairJson.addProperty("id", pair.getID());
        pairJson.addProperty("pairScore", pair.getPairScore());
        pairJson.addProperty("position", position);
        pairJson.addProperty("isPlaying", isPlaying);
        if (timeSlots.containsKey(pair)) {
            pairJson.addProperty("timeSlot", timeSlots.get(pair).toString());
        }
        return pairJson;
    }

    @Override
    public JsonElement serialize(GameSession src, Type typeOfSrc, JsonSerializationContext context) {
        List<Pair> pairList = src.getAllPairs();
        Set<Pair> activePairs = src.getActivePairSet();
        Map<Pair, Time> timeSlots = src.getTimeSlots();
        Map<Pair, Integer> positionChanges = src.getPositionChanges();
        Date ladderModificationDate = src.getLadderModificationDate();

        JsonObject ladderObject = new JsonObject();
        ladderObject.addProperty("timeStamp", ladderModificationDate.toString());
        JsonArray pairsArray = new JsonArray();
        int position = 1;
        for (Pair pair : pairList) {
            JsonObject pairJson = getPairJsonObject(pair, timeSlots, position,
                    activePairs.contains(pair));
            pairJson.addProperty("positionChange", positionChanges.getOrDefault(pair, 0));
            position++;
            pairsArray.add(pairJson);
        }
        ladderObject.add("pairs", pairsArray);
        return ladderObject;
    }
}
