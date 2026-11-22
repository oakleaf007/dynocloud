package com.dyno.dto;

import java.util.UUID;

public class FileDownloadInfo {

    private final UUID fileId;
    private final String objectKey;
    private final String fileName;
    private final String contentType;
    private final long size;
    private final String encryptedDek;
    private final byte[] fileIv;

    public FileDownloadInfo(
            UUID fileId,
            String objectKey,
            String fileName,
            String contentType,
            long size,
            String encryptedDek,
            byte[] fileIv) {

        this.fileId = fileId;
        this.objectKey = objectKey;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.encryptedDek = encryptedDek;
        this.fileIv = fileIv.clone();
    }

    public UUID getFileId() {
        return fileId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public String getEncryptedDek() {
        return encryptedDek;
    }

    public byte[] getFileIv() {
        return fileIv.clone();
    }
}