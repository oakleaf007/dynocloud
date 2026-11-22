
package com.dyno.dto;

import java.nio.file.Path;

public class EncryptionResult {

    private Path encryptedFile;

    private String encryptedDek;

    private byte[] fileIv;

    public EncryptionResult(
            Path encryptedFile,
            String encryptedDek,
            byte[] fileIv
    ) {
        this.encryptedFile = encryptedFile;
        this.encryptedDek = encryptedDek;
        this.fileIv = fileIv.clone();
    }

    public Path getEncryptedFile() {
        return encryptedFile;
    }

    public void setEncryptedFile(Path encryptedFile) {
        this.encryptedFile = encryptedFile;
    }

    public String getEncryptedDek() {
        return encryptedDek;
    }

    public void setEncryptedDek(String encryptedDek) {
        this.encryptedDek = encryptedDek;
    }

    public byte[] getFileIv() {
        return fileIv.clone();
    }

    public void setFileIv(byte[] fileIv) {
        this.fileIv = fileIv.clone();
    }
}
