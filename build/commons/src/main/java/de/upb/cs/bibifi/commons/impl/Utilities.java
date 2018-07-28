package de.upb.cs.bibifi.commons.impl;

import com.google.gson.Gson;
import de.upb.cs.bibifi.commons.dto.TransmissionPacket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class Utilities {

    //Json Deserializer
    public static TransmissionPacket deserializer (String text){
        Gson gson = new Gson();
        return gson.fromJson(text, TransmissionPacket.class);
    }
    //Json Serializer
    public static String Serializer (TransmissionPacket transmissionPacket){
        Gson gson = new Gson();
        return gson.toJson(transmissionPacket);
    }

    public static InputStream convertString(String str) {
        byte[] array = str.getBytes(StandardCharsets.ISO_8859_1);
        return new ByteArrayInputStream(array);
    }

    public static String convertOutputStream(OutputStream outputStream) {
        byte[] array = ((ByteArrayOutputStream) outputStream).toByteArray();
        return new String(array, StandardCharsets.ISO_8859_1);
    }
}
