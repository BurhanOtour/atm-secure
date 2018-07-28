package de.upb.cs.bibifi.commons.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Utilities {

    public static InputStream convertString(String str) {
        byte[] array = str.getBytes(StandardCharsets.ISO_8859_1);
        return new ByteArrayInputStream(array);
    }

    public static String convertOutputStream(OutputStream outputStream) {
        byte[] array = ((ByteArrayOutputStream) outputStream).toByteArray();
        return new String(array, StandardCharsets.ISO_8859_1);
    }
}
