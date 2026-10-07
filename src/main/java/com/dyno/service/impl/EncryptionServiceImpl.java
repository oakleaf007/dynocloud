package com.dyno.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.dyno.config.EncryptionProperties;
import com.dyno.dto.EncryptionResult;
import com.dyno.service.EncryptionService;

@Service
public class EncryptionServiceImpl implements EncryptionService {
	
	private static final int AES_KEY_SIZE =256;
	private static final int GCM_TAG_LENGTH = 128;
	private static final int IV_LENGTH = 12;
	private final SecureRandom secureRandom = new SecureRandom();
	private final SecretKey kek;
	
	public EncryptionServiceImpl(EncryptionProperties properties) {
		byte[] keyBytes= hexToByte(properties.getKek());
		if(keyBytes.length !=32) {
			throw new IllegalArgumentException(
					"KEK must be exactly 32 bytes"
					);
		}
		
		this.kek = new SecretKeySpec(keyBytes, "AES");
	}
	
	
	@Override
	public EncryptionResult encrypt(Path source) throws IOException, GeneralSecurityException{
		// TODO Auto-generated method stub
		SecretKey dek = generateDek();
		
		byte[] fileIv = generateIv();
		
		byte[] dekIv = generateIv();
		
		byte[] encryptedDek = encryptDek(dek, dekIv);
		
		Path encryptedFile = Files.createTempFile("dynocloud-", ".enc");
		
		encryptFile(source, encryptedFile, dek, fileIv);
		
		return new EncryptionResult(
				encryptedFile,
				encryptedDek,
				fileIv,
				dekIv);
		
		
		
		

	}
	private byte[] generateIv() {
		byte[] iv = new byte[IV_LENGTH];
		
		secureRandom.nextBytes(iv);
		
		return iv;
	}
	private SecretKey generateDek() throws GeneralSecurityException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		
		keyGen.init(AES_KEY_SIZE);
		
		return keyGen.generateKey();
	
	}
	
	
	private byte[] encryptDek(SecretKey dek, byte[] iv) throws GeneralSecurityException{
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmSpec = new GCMParameterSpec(
				GCM_TAG_LENGTH, iv);
		cipher.init(Cipher.ENCRYPT_MODE, kek, gcmSpec);
		return cipher.doFinal(dek.getEncoded());
	}
	
	private void encryptFile(Path source, Path destination, SecretKey dek, byte[] iv) throws IOException, GeneralSecurityException {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec gcmSpec =new GCMParameterSpec(
						GCM_TAG_LENGTH, iv
						);
			cipher.init(cipher.ENCRYPT_MODE,dek, gcmSpec);
			
			try(
				InputStream input = Files.newInputStream(source);
					OutputStream output = Files.newOutputStream(destination)
				){
				byte[] buffer = new byte[8192];
				int bytesRead;
				
				while((bytesRead = input.read(buffer)) != -1) {
					byte[] encrypted = cipher.update(buffer,
							0, bytesRead
							);
					if(encrypted !=null) {
						output.write(encrypted);
					}
				}
				byte[] finalBytes = cipher.doFinal();
				output.write(finalBytes);
				
			}
	}
	
	private byte[] hexToByte(String hex) {
		if(hex == null || hex.length() % 2 !=0) {
			throw new IllegalArgumentException("Invalid KEK");
		}
		byte[] bytes = new byte[hex.length()/2];
		
		for(int i =0; i<hex.length(); i += 2) {
			bytes[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2),16);
		}
		
		return bytes;
	}
	



	@Override
	public void decrypt(Path encryptFile, Path outputFile, byte[] encryptedDek, byte[] fileIv, byte[] dekIv)
			throws IOException, GeneralSecurityException {
		// TODO Auto-generated method stub
		
		SecretKey dek = decryptDek(encryptedDek, dekIv);
		
		decryptFile(encryptFile, outputFile, dek, fileIv);
		
	}
	
	private SecretKey decryptDek(
			byte[] encryptedDek, byte[] dekIv
			) throws GeneralSecurityException{
		
Cipher cipher= Cipher.getInstance("AES/GCM/NoPadding");
		
		GCMParameterSpec spec = new GCMParameterSpec(
					GCM_TAG_LENGTH, dekIv
				);
		cipher.init(Cipher.DECRYPT_MODE, kek, spec);
		
		byte[] dekBytes = cipher.doFinal(encryptedDek);
		return new SecretKeySpec(dekBytes, "AES");
	}
	
	private void decryptFile(
			Path encryptedFile, Path outputFile,
			SecretKey dek, byte[] fileIv
			) throws IOException, GeneralSecurityException{
          Cipher cipher= Cipher.getInstance("AES/GCM/NoPadding");
		
		GCMParameterSpec spec = new GCMParameterSpec(
					GCM_TAG_LENGTH, fileIv
				);
		cipher.init(Cipher.DECRYPT_MODE, dek, spec);
		
		try(
				InputStream input = Files.newInputStream(encryptedFile);
				OutputStream output = Files.newOutputStream(outputFile)
				){
			byte[] buffer = new byte[8192];
			
			int bytesRead;
			
			while((bytesRead = input.read(buffer))!=-1) {
				
				byte[] decrypted = cipher.update(buffer, 0, bytesRead);
				
				if(decrypted != null) {
					output.write(decrypted);
				}
			}
			byte[] finalBytes = cipher.doFinal();
			
			output.write(finalBytes);
			
			
		}
	}

}
