package com.toptal.mealsmobileapp.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateJsonSerializer implements JsonDeserializer, JsonSerializer {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateStr = json.getAsString();
        try {
            return strToDate(dateStr);
        }
        catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        String dateStr = dateToStr((Date)src);
        return new JsonPrimitive(dateStr);
    }

    public static Date strToDate(String s) throws ParseException {
        if (s.isEmpty()) {
            return null;
        }
        return format.parse(s);
    }

    public static String dateToStr(Date d) {
        return format.format(d);
    }
}
