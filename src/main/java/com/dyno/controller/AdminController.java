package com.dyno.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyno.dto.admin.StorageOverviewDTO;
import com.dyno.service.StorageAnalyticsService;

@RestController
@RequestMapping("/api/v1/admin/storage")
public class AdminController {
	private final  StorageAnalyticsService storageAnalyticsService;

	public AdminController(StorageAnalyticsService storageAnalyticsService) {
		super();
		this.storageAnalyticsService = storageAnalyticsService;
	}
	
	@GetMapping("/overview")
    public ResponseEntity<StorageOverviewDTO> storageOverview(){
    	
		StorageOverviewDTO res = storageAnalyticsService.getGlobalStorageOverview();
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(res);
    }
        
        
    
}
