package de.upd.cd.bibifi.commons;

import de.upd.cd.bibifi.commons.impl.EncryptionImpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EncryptionTest {

    @Test
    public void EncryptionTest() throws Exception{
        String helloMessage = "Hello encryption";
        String key = "1234567891012141";
        IEncryption encryption = new EncryptionImpl(new String(key));

        OutputStream outStreamEnc = encryption.encryptMessage(helloMessage);
        InputStream inputStreamDec = new ByteArrayInputStream(((ByteArrayOutputStream) outStreamEnc).toByteArray());

        String decodedString = encryption.decryptMessage(inputStreamDec);

        assertEquals(helloMessage, decodedString);
    }
}
