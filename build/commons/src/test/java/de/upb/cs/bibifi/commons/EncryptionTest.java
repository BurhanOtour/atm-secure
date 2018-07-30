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

    @Test
    public void EncryptionTest() throws Exception {
        String helloMessage = "Hello encryption";
        String key = "PrvQH+6bvZPJrqR02ntOFw";
        IEncryption encryption = EncryptionImpl.initialize(key);

        String encryptedString = encryption.encryptMessage(helloMessage);
        String decryptedString = encryption.decryptMessage(encryptedString);

        assertEquals(helloMessage, decryptedString);
    }
}
