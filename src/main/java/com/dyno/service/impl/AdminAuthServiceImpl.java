package com.dyno.service.impl;

import java.time.Instant;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.dyno.dto.admin.AdminLoginRequest;
import com.dyno.dto.admin.AdminLoginResponse;
import com.dyno.entity.Admin;
import com.dyno.repository.AdminRepository;
import com.dyno.service.AdminAuthService;
import com.dyno.service.JwtService;
import com.dyno.util.AdminKeyVerifier;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {
	
	
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final AdminKeyVerifier adminKeyVerifier;
    private final JwtService jwtService;

    public AdminAuthServiceImpl(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder, AdminKeyVerifier adminKeyVerifier, JwtService jwtService) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
		this.adminKeyVerifier = adminKeyVerifier;
		this.jwtService = jwtService;
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
    	
        if (adminRepository.count() == 0) {

            // Validate external bootstrap key
            if (!adminKeyVerifier.verifyAdminKey(request.adminKey())) {
                throw new BadCredentialsException(
                    "Invalid admin credentials"
                );
            }
            Admin admin = new Admin();

            admin.setUsername(request.username());
          
            admin.setPassword(
                passwordEncoder.encode(request.password())
            );
            admin.setEnabled(true);
            admin.setCreatedAt(Instant.now());

            admin = adminRepository.saveAndFlush(admin);

            return generateToken(admin);
    }
       
        
        
        Admin admin = adminRepository
                .findByUsername(request.username())
                .orElseThrow(() ->
                    new BadCredentialsException(
                        "Invalid admin credentials"
                    )
                );

        if (!admin.isEnabled() ||
            !passwordEncoder.matches(
                request.password(),
                admin.getPassword()
            )) {

            throw new BadCredentialsException(
                "Invalid admin credentials"
            );
           
        }
        return generateToken(admin);

    }
    private AdminLoginResponse generateToken(Admin admin) {

        Instant now = Instant.now();
        long expiry = 900;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("dynocloud")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(admin.getId().toString())
                .claim("username", admin.getUsername())
                .claim("role", "ADMIN")
                .claim("principal_type", "ADMIN")
                .build();

        String token = jwtEncoder.encode(
            JwtEncoderParameters.from(claims)
        ).getTokenValue();

        return new AdminLoginResponse(
        		"Admin signup/login successfull",
            token,
            "Bearer",
            expiry
        );
    }
    

}