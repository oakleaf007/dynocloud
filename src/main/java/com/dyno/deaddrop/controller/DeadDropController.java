package com.dyno.deaddrop.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.dyno.deaddrop.app.DeadDropDownloadService;
import com.dyno.deaddrop.app.DeadDropUploadService;
import com.dyno.deaddrop.dto.DeadDropDownloadResult;
import com.dyno.deaddrop.dto.DeadDropUploadRequest;
import com.dyno.deaddrop.dto.DeadDropUploadResponse;

@RestController
@RequestMapping("/dead-drops")
public class DeadDropController {

    private final DeadDropUploadService uploadService;
    private final DeadDropDownloadService downloadService;
    public DeadDropController(
            DeadDropUploadService uploadService, DeadDropDownloadService downloadService
    ) {
        this.uploadService = uploadService;
		this.downloadService = downloadService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    
    public ResponseEntity<DeadDropUploadResponse> upload(
            @RequestPart("file") MultipartFile file
    ) throws IOException, GeneralSecurityException {

        DeadDropUploadRequest request =
                new DeadDropUploadRequest(
                        file,
                       file.getOriginalFilename(),
                       file.getContentType()
                );

        DeadDropUploadResponse response =
                uploadService.upload(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
}