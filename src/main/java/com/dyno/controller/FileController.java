package com.dyno.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dyno.dto.DownloadResponse;
import com.dyno.dto.FileResponse;
import com.dyno.dto.RenameFileRequest;
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

		System.out.println("========== UPLOAD ==========");
	    System.out.println("Name: " + file.getOriginalFilename());
	    System.out.println("Content-Type: " + file.getContentType());
	    System.out.println("Size: " + file.getSize());

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
	@GetMapping("/{fileId}/download")
	public ResponseEntity<DownloadResponse> download(@PathVariable("fileId") UUID fileId,
			Authentication authentication){
		String username = authentication.getName();
		
		String url = fileService.generateDownloadUrl(username, fileId);
		
		return ResponseEntity.ok(
				new DownloadResponse(url));
		
	}
	
	@DeleteMapping("/delete/{fileId}")
	public ResponseEntity<Void> deleteFile(
			@PathVariable("fileId") UUID fileId,
				Authentication authentication
			){
	
	String username = authentication.getName();
	fileService.deleteFile(username, fileId);
	
	
	return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/rename/{fileId}")
	public ResponseEntity<FileResponse> renameFile(
			@PathVariable("fileId") UUID fileId,
			@RequestBody RenameFileRequest request,
			Authentication authentication
			){
		
		
		User user = userService.getUserByUsername(authentication.getName());
		
		FileResponse response =fileService.renameFile(user, fileId, request.getNewName());
		
		return ResponseEntity.ok(response);
	}
	
	
	
	

}
