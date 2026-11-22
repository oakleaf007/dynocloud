package com.dyno.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.dyno.dto.DecryptedFile;
import com.dyno.dto.DownloadResponse;
import com.dyno.dto.DownloadTicketResponse;
import com.dyno.dto.FileDownloadInfo;
import com.dyno.dto.FileResponse;
import com.dyno.dto.RenameFileRequest;
import com.dyno.entity.File;
import com.dyno.entity.User;
import com.dyno.service.FileService;
import com.dyno.service.UserService;
import com.dyno.service.impl.DownloadTicketService;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
	private final FileService fileService;
	private final UserService userService;
	private final DownloadTicketService downloadTicketService;
	public FileController(FileService fileService, UserService userService, DownloadTicketService downloadTicketService) {
		super();
		this.fileService = fileService;
		this.userService=userService;
		this.downloadTicketService = downloadTicketService;
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
//	@GetMapping("/{fileId}/download-temp")
//	public ResponseEntity<DownloadResponse> downloadTemp(@PathVariable("fileId") UUID fileId,
//			Authentication authentication){
//		String username = authentication.getName();
//		
//		String url = fileService.generateDownloadUrl(username, fileId);
//		
//		return ResponseEntity.ok(
//				new DownloadResponse(url));
//		
//	}
	
	@GetMapping("/{fileId}/download")
	public ResponseEntity<Resource> download( @PathVariable("fileId") UUID fileId,Authentication auth ) throws IOException{
		User user = userService.getUserByUsername(auth.getName());
		
		DecryptedFile decryptedFile = fileService.download(fileId, user);
		
		Resource resource = new FileSystemResource(decryptedFile.getPath());
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(decryptedFile.getContentType()))
				.contentLength(decryptedFile.getSize())
				.header(
						HttpHeaders.CONTENT_DISPOSITION,
						"attachment: filename=\""+
				         decryptedFile.getFileName()+ "\""
						)
				.body(resource);
		
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
	
	@GetMapping("/{fileId}/download/stream")
	public ResponseEntity<StreamingResponseBody> streamDownload(
	        @PathVariable("fileId") UUID fileId,
	        Authentication auth) {

	    User user = userService.getUserByUsername(auth.getName());

	    // Check ownership and prepare file metadata.
	    FileDownloadInfo info =
	            fileService.prepareStreamingDownload(fileId, user);

	    // Stream encrypted Garage data -> chunk decryption -> HTTP response.
	    StreamingResponseBody responseBody = output -> {
	        fileService.streamDownload(info, output);
	    };

	    return ResponseEntity.ok()
	            .contentType(resolveContentType(info.getContentType()))
	            .contentLength(info.getSize())
	            .header(
	                    HttpHeaders.CONTENT_DISPOSITION,
	                    ContentDisposition.attachment()
	                            .filename(
	                                    info.getFileName(),
	                                    StandardCharsets.UTF_8)
	                            .build()
	                            .toString()
	            )
	            .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
	            .header("X-Content-Type-Options", "nosniff")
	            .body(responseBody);
	}
	
	private MediaType resolveContentType(String contentType) {

	    if (contentType == null || contentType.isBlank()) {
	        return MediaType.APPLICATION_OCTET_STREAM;
	    }

	    try {
	        return MediaType.parseMediaType(contentType);
	    } catch (IllegalArgumentException ex) {
	        return MediaType.APPLICATION_OCTET_STREAM;
	    }
	}
	

//	for native browser download
@PostMapping("/{fileId}/download-ticket")
public ResponseEntity<DownloadTicketResponse> createDownloadTicket(
        @PathVariable("fileId") UUID fileId,
        Authentication authentication) {

    User user = userService.getUserByUsername(
            authentication.getName()
    );

    String ticket = downloadTicketService.createTicket(
            fileId, user
    );

    return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-store")
            .body(new DownloadTicketResponse(ticket));
}

//for native browser download
@GetMapping("/download/{ticket}")
public ResponseEntity<StreamingResponseBody> downloadWithTicket(
        @PathVariable("ticket") String ticket) {

    
    var downloadTicket =
            downloadTicketService.consumeTicket(ticket);

    // Recheck ownership and prepare streaming download.
    FileDownloadInfo info =
            fileService.prepareStreamingDownload(
                    downloadTicket.getFileId(),
                    downloadTicket.getUser()
            );

    StreamingResponseBody responseBody = output -> {
        fileService.streamDownload(info, output);
    };

    return ResponseEntity.ok()
            .contentType(resolveContentType(info.getContentType()))
            .contentLength(info.getSize())
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                            .filename(
                                    info.getFileName(),
                                    StandardCharsets.UTF_8
                            )
                            .build()
                            .toString()
            )
            .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
            .header("X-Content-Type-Options", "nosniff")
            .body(responseBody);
}


}
