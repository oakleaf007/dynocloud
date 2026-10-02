package com.dyno.dto;

import jakarta.validation.constraints.NotBlank;

public class DownloadResponse {
	@NotBlank
	String url;

	

	public DownloadResponse(@NotBlank String url) {
	
		this.url = url;
	}
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
