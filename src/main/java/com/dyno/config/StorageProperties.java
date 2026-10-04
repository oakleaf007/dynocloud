package com.dyno.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix= "dynocloud.storage")
public class StorageProperties {
	private long defaultQuotaGb;

	public long getDefaultQuotaGb() {
		return defaultQuotaGb;
	}

	public void setDefaultQuotaGb(long defaultQuotaGb) {
		this.defaultQuotaGb = defaultQuotaGb;
	}
	
	public long getDefaultQuotaBytes() {
		return defaultQuotaGb *1024*1024*1024;
	}

}
