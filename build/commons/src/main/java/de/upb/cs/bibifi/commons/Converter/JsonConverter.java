package de.upb.cs.bibifi.commons.Converter;

import com.google.gson.Gson;
import de.upb.cs.bibifi.commons.dto.Request;


public class JsonConverter {

    //Json Deserializer
    public static Request deserializer (String text){
        Gson gson = new Gson();
        return gson.fromJson(text, Request.class);
    }

    //Json Serializer
    public static String Serializer (Request request){
        Gson gson = new Gson();
        return gson.toJson(request);
    }
}
