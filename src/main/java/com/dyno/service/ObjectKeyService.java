package com.dyno.service;

import java.util.UUID;

public interface ObjectKeyService {
	
	String generate(UUID userId, UUID fileId);
}
