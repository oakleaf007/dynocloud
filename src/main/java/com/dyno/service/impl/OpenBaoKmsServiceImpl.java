package com.dyno.service.impl;

import java.util.Base64;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.dyno.config.OpenBaoProperties;
import com.dyno.service.OpenBaoKmsService;

@Service
public class OpenBaoKmsServiceImpl implements OpenBaoKmsService {
	
	
	 private final RestClient restClient;
	    private final OpenBaoTokenService tokenService;

	    private static final String KEY_NAME = "dynocloud-kek";
	
	    public OpenBaoKmsServiceImpl(
	            OpenBaoProperties properties,
	            OpenBaoTokenService tokenService) {

	        this.restClient = RestClient.builder()
	                .baseUrl(properties.getAddress())
	                .build();

	        this.tokenService = tokenService;
	    }
	    
	    
	    @Override
	public String encryptDek(byte[] dek) {
	    	String plaintext = Base64.getEncoder()
	                .encodeToString(dek);

	        Map<String, String> request = Map.of(
	                "plaintext", plaintext
	        );

	        Map<?, ?> response = restClient.post()
	                .uri("/v1/transit/encrypt/" + KEY_NAME)
	                .header("X-Vault-Token", tokenService.getToken())
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(request)
	                .retrieve()
	                .body(Map.class);

	        if (response == null ||
	                !(response.get("data") instanceof Map<?, ?> data)) {

	            throw new IllegalStateException(
	                    "Invalid OpenBao encryption response"
	            );
	        }

	        Object ciphertext = data.get("ciphertext");

	        if (!(ciphertext instanceof String encryptedDek)
	                || encryptedDek.isBlank()) {

	            throw new IllegalStateException(
	                    "OpenBao did not return encrypted DEK"
	            );
	        }

	        return encryptedDek;
	}

	@Override
	public byte[] decryptDek(String encryptedDek) {
		Map<String, String> request = Map.of(
                "ciphertext", encryptedDek
        );

        Map<?, ?> response = restClient.post()
                .uri("/v1/transit/decrypt/" + KEY_NAME)
                .header("X-Vault-Token", tokenService.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null ||
                !(response.get("data") instanceof Map<?, ?> data)) {

            throw new IllegalStateException(
                    "Invalid OpenBao decryption response"
            );
        }

        Object plaintext = data.get("plaintext");

        if (!(plaintext instanceof String decoded)
                || decoded.isBlank()) {

            throw new IllegalStateException(
                    "OpenBao did not return decrypted DEK"
            );
        }

        return Base64.getDecoder().decode(decoded);
	}

}