package com.dyno.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dyno.service.ObjectKeyService;

@Service
public class ObjectKeyImpl implements ObjectKeyService{

	@Override
	public String generate(UUID userId, UUID fileId) {
	return "users/"
			+ userId
			+ "/files/"
			+ fileId;
	}
	

}
