package com.dyno.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.dto.FileResponse;
import com.dyno.entity.File;
import com.dyno.entity.User;
import com.dyno.service.FileService;
import com.dyno.service.UserService;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
	private final FileService fileService;
	private final UserService userService;

	public FileController(FileService fileService, UserService userService) {
		super();
		this.fileService = fileService;
		this.userService=userService;
	}
	
	@PostMapping("/upload/upload-file")
	public ResponseEntity<?> upload(
				@RequestParam("file") MultipartFile file,
				Authentication authentication
			) throws IOException{
		
		return ResponseEntity.ok().build();
		
	}
	
	@PostMapping("/upload/upload-test")
	public ResponseEntity<FileResponse> uploadTest(
	        @RequestParam("file") MultipartFile file,
	        Authentication authentication) throws IOException {

	    User user = userService.getUserByUsername(authentication.getName());

	    File storedFile = fileService.upload(file, user);
	    
	    FileResponse res = new FileResponse(
	    		  storedFile.getId(),
	              storedFile.getOriginalName(),
	              storedFile.getObjectKey(),
	              storedFile.getContentType(),
	              storedFile.getSizeBytes(),
	              storedFile.getCreatedAt(),
	              storedFile.getOwner().getId()
	    		);

	    return ResponseEntity.ok(res);
	}
	
	
	
	

}
