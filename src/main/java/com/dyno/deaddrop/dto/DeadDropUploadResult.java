package com.dyno.deaddrop.dto;

public class DeadDropUploadResult {

    private final String publicUrl;

    public DeadDropUploadResult(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }
}