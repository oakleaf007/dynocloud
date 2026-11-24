package com.dyno.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyno.dto.LoginReq;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/session")
public class SessionAuthController {
	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;
	public SessionAuthController(AuthenticationManager authenticationManager,
			SecurityContextRepository securityContextRepository) {
		super();
		this.authenticationManager = authenticationManager;
		this.securityContextRepository = securityContextRepository;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(
			@RequestBody LoginReq request,
			HttpServletRequest httpRequest,
			HttpServletResponse httpResponse
			){
		
		Authentication auth = authenticationManager.authenticate(
				
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword())
				);
		HttpSession existingSession = httpRequest.getSession(false);
		if(existingSession != null) {
			httpRequest.changeSessionId();
		}
		
		SecurityContext context =
				SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		
		SecurityContextHolder.setContext(context);
		
		securityContextRepository.saveContext(
				context, httpRequest, httpResponse);
		
		return ResponseEntity.ok(
				Map.of(
						"status", "authenticated",
						"username",  auth.getName()
						
						)
				
				);
		
	}

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "authenticated", true,
                        "username", authentication.getName()
                )
        );
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
    		HttpServletRequest req,
    		HttpServletResponse res
    		){
    	
    	HttpSession session = req.getSession();
    	if(session != null) {
    		session.invalidate();
    	}
    	
    	SecurityContextHolder.clearContext();
    	
    	return ResponseEntity.ok(Map.of("status", "logged_out"));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

}
