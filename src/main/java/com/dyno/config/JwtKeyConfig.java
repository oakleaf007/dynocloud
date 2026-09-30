package com.dyno.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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
	
/*	It is responsible for reading the private key from the
 * pem file and store as JAVA RSA key

*/
	@Bean
	public RSAPrivateKey privateKey() throws Exception{
		return RsaKeyConverters.pkcs8()
				.convert(
						new ClassPathResource(
								"keys/private.pem"
								).getInputStream()
						);
				
	}
	
/*	It is responsible for reading the public key from the
	 * pem file and store as JAVA RSA key

	*/
	@Bean
	public RSAPublicKey publicKey() throws Exception{
		return RsaKeyConverters.x509()
				.convert(new ClassPathResource(
						"keys/public.pem"
						).getInputStream()
				);
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
