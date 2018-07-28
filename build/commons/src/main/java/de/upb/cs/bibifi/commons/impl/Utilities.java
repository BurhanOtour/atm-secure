package de.upb.cs.bibifi.commons.impl;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Utilities {

    public static InputStream CovertString(String str){
        byte[] array = str.getBytes(StandardCharsets.ISO_8859_1);
        return  new ByteArrayInputStream(array);
    }
    public static String ConvertOutputStream(OutputStream outputStream){
        byte[] array = ((ByteArrayOutputStream) outputStream).toByteArray();
        return  new String(array, StandardCharsets.ISO_8859_1);
    }
}
