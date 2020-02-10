package com.toptal.mealsmobileapp.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Time;
import java.text.ParseException;

import static java.lang.String.format;

public class TimeJsonSerializer implements JsonDeserializer, JsonSerializer {

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String timeStr = json.getAsString();
        try {
            return strToTime(timeStr);
        }
        catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        String timeStr = timeToStr((Time)src);
        return new JsonPrimitive(timeStr);
    }

    public static Time strToTime(String s) throws ParseException {
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Time.valueOf(s);
        }
        catch (Exception e) {
            throw new ParseException(format("Could not parse string for time [%s]", s), 0);
        }
    }

    public static String timeToStr(Time t) {
            return t.toString();
    }


}
