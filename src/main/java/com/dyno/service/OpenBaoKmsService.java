package com.dyno.service;

public interface OpenBaoKmsService {
	String encryptDek(byte[] dek);

    byte[] decryptDek(String encryptedDek);
}
