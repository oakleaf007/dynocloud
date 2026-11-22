package com.dyno.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.dto.DecryptedFile;
import com.dyno.dto.FileDownloadInfo;
import com.dyno.dto.FileResponse;
import com.dyno.entity.File;
import com.dyno.entity.User;

public interface FileService {

	File upload(MultipartFile file,
				User user) throws IOException;
	
	Page<FileResponse> getUserFiles(String username,
			Pageable pageable);
	
//	String generateDownloadUrl(String username, UUID fileId);
	
	DecryptedFile download(UUID fileId, User user) throws IOException;
	
	String deleteFile(String username, UUID fileId);
	
	FileResponse renameFile(User user, UUID fileId, String newName);
	FileDownloadInfo prepareStreamingDownload(UUID fileId, User user);

	void streamDownload(FileDownloadInfo info, OutputStream output)
	        throws IOException;
	
}
