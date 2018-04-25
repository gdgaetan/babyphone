package com.example.gaetan.babyphoneapp;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class TokenSerializer implements JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException
    {
        return je.getAsJsonObject().get("token").getAsString();
    }
}
