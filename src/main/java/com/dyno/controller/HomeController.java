package com.dyno.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public Map<String, String> home(){
		return Map.of(
				"application", "Dynocloud",
				"status", "Running",
				"message", "DynoCloud backend is up & running"
				);
	}
}
