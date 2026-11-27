package com.dyno.deaddrop.dto;

import java.nio.file.Path;

public record DeadDropDownloadResult(
        Path file,
        String originalFilename,
        String contentType,
        long contentLength
) {}