package com.dyno.config;

import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JwtKeyConfig {
	
	@Value("${app.security.private-key-path}")
	private Resource privateKeyResource;
	
	@Value("${app.security.public-key-path}")
	private Resource publicKeyResource;
	
/*	It is  for reading the private key from the
 * pem file and store as JAVA RSA key

*/
	@Bean
	public RSAPrivateKey privateKey() throws Exception{
		try(InputStream inputStream = privateKeyResource.getInputStream()){
			return RsaKeyConverters.pkcs8().convert(inputStream);
		}
				
	}
	
/*	It is  for reading the public key from the
	 * pem file and store as JAVA RSA key

	*/
	@Bean
	public RSAPublicKey publicKey() throws Exception{
		try(InputStream inputStream = publicKeyResource.getInputStream()){
			return RsaKeyConverters.x509().convert(inputStream);
		}
	}
	
//	this is for encoding the jwt
	
	@Bean
	public JwtEncoder jwtEncoder(
			RSAPublicKey publicKey,
	RSAPrivateKey privatekey) {
		
	
		RSAKey rsakey = new RSAKey.Builder(publicKey)
				.privateKey(privatekey)
				.build();
		
		JWKSource<SecurityContext> jwkSource=
				new ImmutableJWKSet<>(new JWKSet(rsakey));
		
//		actual jwt creation and returning it
		return new NimbusJwtEncoder(jwkSource);
	}
	
	
//	this is for decoding the jwt
	@Bean
	public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
		return NimbusJwtDecoder
				.withPublicKey(publicKey)
				.build();
	}
	
}
