package de.upb.cs.bibifi.commons.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.upb.cs.bibifi.commons.IEncryption;


public class EncryptionImpl implements IEncryption {

	private static String algoName = "AES";

	private static String transformation = "AES/CBC/PKCS5Padding";

	private byte[] key;

	public EncryptionImpl(String key){
		this.key = key.getBytes();
	}

    public OutputStream encryptMessage(String message) throws IOException{
		try {
			OutputStream outputStream = new ByteArrayOutputStream();
			SecretKey secKey = new SecretKeySpec(key, algoName);
			Cipher aes = Cipher.getInstance(transformation);
			aes.init(Cipher.ENCRYPT_MODE, secKey, new IvParameterSpec(key));
			byte[] encryptedArray = aes.doFinal(message.getBytes());
			outputStream.write(encryptedArray);
			return  outputStream;
		}catch (NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException |
				InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException ex){
			throw new IOException("Error happened with encryption the message");
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
		}catch (NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException |
				InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException ex){
			throw new IOException("Error happened with decryption the message");
		}
    }

    private byte[] readBytes(InputStream inputStream) throws IOException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[0xFFFF];
		for (int len = inputStream.read(buffer); len != -1; len = inputStream.read(buffer)) {
			os.write(buffer, 0, len);
		}
		return os.toByteArray();
	}
}
