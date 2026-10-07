package com.dyno.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="dynocloud.encryption")
public class EncryptionProperties {

	private String kek;

	public String getKek() {
		return kek;
	}

	public void setKek(String kek) {
		this.kek = kek;
	}
}
