package com.dyno.service;

import java.io.InputStream;
import java.nio.file.Path;

public interface StorageService {
	String upload(
			Path file,
			long contentLength,
			String contentType,
			String objectKey
			);
	
	String generateDownloadUrl(String objectKey, String contentType);
	
	void download(String objectKey, Path destination);
	
	void delete(String objectKey);
	

}
