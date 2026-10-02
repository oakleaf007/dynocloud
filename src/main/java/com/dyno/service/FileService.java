package com.dyno.service;


import java.io.IOException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.dto.FileResponse;
import com.dyno.entity.File;
import com.dyno.entity.User;

public interface FileService {

	File upload(MultipartFile file,
				User user) throws IOException;
	
	Page<FileResponse> getUserFiles(String username,
			Pageable pageable);
	
	String generateDownloadUrl(String username, UUID fileId);

}
