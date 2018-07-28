package de.upb.cs.bibifi.commons;

import de.upb.cs.bibifi.commons.impl.EncryptionImpl;

import de.upb.cs.bibifi.commons.impl.Utilities;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EncryptionTest {

//    @Test
//    public void EncryptionTest() throws Exception{
//        String helloMessage = "Hello encryption";
//        String key = "1234567891012141";
//        IEncryption encryption = EncryptionImpl.initialize(key);
//
//        OutputStream outStreamEnc = encryption.encryptMessage(helloMessage);
//        InputStream inputStreamDec = new ByteArrayInputStream(((ByteArrayOutputStream) outStreamEnc).toByteArray());
//
//        String decodedString = encryption.decryptMessage(inputStreamDec);
//
//        String encryptedString = Utilities.convertOutputStream(encryption.encryptMessage(helloMessage));
//        String decryptedString = encryption.decryptMessage(Utilities.convertString(encryptedString));
//
//        assertEquals(helloMessage, decryptedString);
//        assertEquals(helloMessage, decodedString);
//    }
}
