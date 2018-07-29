package de.upb.cs.bibifi.commons.impl;

import de.upb.cs.bibifi.commons.IEncryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class EncryptionImpl implements IEncryption {

    private static String algoName = "AES";

   private static String transformation = "AES/CBC/PKCS5Padding";
 //   private static String transformation = "AES/ECB/NoPadding";
    private byte[] key;

    private static EncryptionImpl singleton;

    private EncryptionImpl() {
    }

    public static EncryptionImpl initialize(String key) {
        if (singleton == null)
            singleton = new EncryptionImpl();

        singleton.key =  Base64.getDecoder().decode(key);
        return singleton;
    }

    public static EncryptionImpl getInstance() throws Exception {
        if (singleton == null)
            throw new Exception("Encryption is not initialized");
        return singleton;
    }

    public OutputStream encryptMessage(String message) throws IOException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            SecretKey secKey = new SecretKeySpec(key, algoName);
            Cipher aes = Cipher.getInstance(transformation);
            aes.init(Cipher.ENCRYPT_MODE, secKey, new IvParameterSpec(key));
            byte[] encryptedArray = aes.doFinal(message.getBytes());
            outputStream.write(encryptedArray);
            return outputStream;
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException |
                InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException ex) {
            throw new IOException("Error happened with encryption the message",ex);
        }
    }

    @Override
    public String encryptMessageString(String message) throws IOException {
        try {
            byte[] encryptedArray = message.getBytes();
            SecretKey secKey = new SecretKeySpec(key, algoName);
            Cipher aes = Cipher.getInstance(transformation);
            aes.init(Cipher.DECRYPT_MODE, secKey, new IvParameterSpec(key));
            byte[] decryptedBytes = aes.doFinal(encryptedArray);
            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException |
                InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException ex) {
            throw new IOException("Error happened with decryption the message", ex);
        }
    }

    public String decryptMessage(InputStream inputStream) throws IOException {
        try {
            byte[] encryptedArray = readBytes(inputStream);
            SecretKey secKey = new SecretKeySpec(key, algoName);
            Cipher aes = Cipher.getInstance(transformation);
            aes.init(Cipher.DECRYPT_MODE, secKey, new IvParameterSpec(key));
            byte[] decryptedBytes = aes.doFinal(encryptedArray);
            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException |
                InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException ex) {
            throw new IOException("Error happened with decryption the message", ex);
        }
    }

    @Override
    public String decryptMessage(String input) throws IOException {
        try {
            byte[] encryptedArray = input.getBytes();
            SecretKey secKey = new SecretKeySpec(key, algoName);
            Cipher aes = Cipher.getInstance(transformation);
            aes.init(Cipher.DECRYPT_MODE, secKey, new IvParameterSpec(key));
            byte[] decryptedBytes = aes.doFinal(encryptedArray);
            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException |
                InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException ex) {
            throw new IOException("Error happened with decryption the message", ex);
        }
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1];
        for (int len = inputStream.read(buffer); len != -1; len = inputStream.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }
}
