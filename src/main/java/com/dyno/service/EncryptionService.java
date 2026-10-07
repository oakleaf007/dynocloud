package com.dyno.service;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import com.dyno.dto.EncryptionResult;

public interface EncryptionService {

	EncryptionResult encrypt(Path source) throws IOException, GeneralSecurityException;
	void decrypt( Path encryptFile,
				Path outputFile,
				byte[] encryptedDek,
				byte[] fileIv,
				byte[] dekIv
			) throws IOException, GeneralSecurityException;
}
