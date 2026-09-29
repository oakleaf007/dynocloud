package com.dyno.service;

import java.io.InputStream;

public interface StorageService {
	String upload(
			byte[] data,
			long contentLength,
			String contentType,
			String objectKey
			);
	
	InputStream download(String objectKey);
	
	void delete(String objectKey);
	

}
