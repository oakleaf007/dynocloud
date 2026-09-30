package com.dyno.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/getFiles")
	public ResponseEntity<Page<FileResponse>> getFiles(
			Authentication authentication,
			@PageableDefault(
					size=20,
					sort= "createdAt",
					direction = Sort.Direction.DESC
					)
			Pageable pageable
			){
		String username = authentication.getName();
		return ResponseEntity.ok(
				fileService.getUserFiles(username, pageable)
				);
		
	}
	
	
	
	

}
