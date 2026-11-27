package com.dyno.deaddrop.dto;

import org.springframework.web.multipart.MultipartFile;

public record DeadDropUploadRequest(
        MultipartFile file,
        String originalFilename,
        String contentType
) {
}