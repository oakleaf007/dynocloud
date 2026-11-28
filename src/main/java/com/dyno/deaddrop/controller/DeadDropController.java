package com.dyno.deaddrop.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.dyno.deaddrop.app.DeadDropDownloadService;
import com.dyno.deaddrop.app.DeadDropUploadService;
import com.dyno.deaddrop.dto.DeadDropDownloadResult;
import com.dyno.deaddrop.dto.DeadDropUploadRequest;
import com.dyno.deaddrop.dto.DeadDropUploadResponse;
import com.dyno.deaddrop.util.DeadDropHtmlResponseBuilder;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/dead-drops")
public class DeadDropController {

    private final DeadDropUploadService uploadService;
    private final DeadDropDownloadService downloadService;
    private final DeadDropHtmlResponseBuilder htmlResponseBuilder;
    public DeadDropController(
            DeadDropUploadService uploadService, DeadDropDownloadService downloadService, DeadDropHtmlResponseBuilder htmlResponseBuilder
    ) {
        this.uploadService = uploadService;
		this.downloadService = downloadService;
		this.htmlResponseBuilder = htmlResponseBuilder;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    
    public RedirectView upload(
            @RequestPart("file") MultipartFile file,
            HttpSession session
    ) throws IOException, GeneralSecurityException {
    	long MAX_FILE_SIZE=500L*1024*1024;
    	if(file.getSize()> MAX_FILE_SIZE) {
    		 throw new IllegalArgumentException("File size exceeds the 500MB limit.");
    	}
        DeadDropUploadRequest request =
                new DeadDropUploadRequest(
                        file,
                       file.getOriginalFilename(),
                       file.getContentType()
                );

        DeadDropUploadResponse response =
                uploadService.upload(request);
        
        session.setAttribute("deadDropResult", response);
        
        RedirectView redirect = new RedirectView(
                "/dead-drops/result",
                true
        );
        
      return redirect;
    }
    
    
    @GetMapping("/{token}")
    public ResponseEntity<StreamingResponseBody> download(
            @PathVariable("token") String token)
            throws IOException, GeneralSecurityException {

        DeadDropDownloadResult result =
                downloadService.prepareDownload(token);

        StreamingResponseBody responseBody = outputStream -> {

            try {
                Files.copy(result.file(), outputStream);
                outputStream.flush();

            } finally {
                Files.deleteIfExists(result.file());
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .contentLength(result.contentLength())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        result.originalFilename(),
                                        StandardCharsets.UTF_8
                                )
                                .build()
                                .toString()
                )
                .body(responseBody);
    }
    @GetMapping(value = "/result", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> showResult(HttpSession session) {

        DeadDropUploadResponse response =
                (DeadDropUploadResponse)
                        session.getAttribute("deadDropResult");

        if (response == null) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body("Result expired or unavailable.");
        }

        String html =
                htmlResponseBuilder.buildResultPage(response);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(html);
    }
    
    @GetMapping("/resolve")
    public ResponseEntity<Void> resolveDownload(
            @RequestParam("token") String token
    ) {

        String extractedToken = extractToken(token);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/dead-drops/" + extractedToken))
                .build();
    }
    private String extractToken(String input) {

        String token = input.trim();

        try {
            // If the input is a complete URL
            if (token.startsWith("http://") ||
                token.startsWith("https://")) {

                URI uri = URI.create(token);

                // Only accept your Dead Drop download path
                String path = uri.getPath();

                if (path == null ||
                    !path.startsWith("/dead-drops/")) {
                    throw new IllegalArgumentException("Invalid Dead Drop URL");
                }

                token = path.substring("/dead-drops/".length());
            }

            // Validate extracted token
            if (token.isBlank() ||
                token.contains("/") ||
                token.contains("\\") ||
                token.contains("..")) {
                throw new IllegalArgumentException("Invalid Dead Drop token");
            }

            return token;

        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid Dead Drop URL or token");
        }
    }
  
}