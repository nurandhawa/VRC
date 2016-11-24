package ca.sfu.teambeta.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;

/**
 * Created by Gordon Shieh on 21/07/16.
 */
public class ScorecardSerializer implements JsonSerializer<Scorecard> {
    @Override
    public JsonElement serialize(Scorecard src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("id", src.getID());
        jsonElement.addProperty("isDone", src.isDone());
        jsonElement.addProperty("timeSlot", src.getTimeSlot().getTimeString());

        JsonArray pairsJsonArray = new JsonArray();
        for (Pair pair : src.getReorderedPairs()) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            JsonObject pairJson = (JsonObject) gson.toJsonTree(pair);
            int pairScore = src.getPairScore(pair);
            if (pairScore != 0) {
                pairJson.addProperty("newRank", pairScore);
            }

            pairsJsonArray.add(pairJson);
        }

        jsonElement.add("pairs", pairsJsonArray);
        return jsonElement;
    }
}
