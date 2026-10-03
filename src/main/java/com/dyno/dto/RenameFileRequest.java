package com.dyno.dto;

import jakarta.validation.constraints.NotNull;

public class RenameFileRequest {

	@NotNull
	private String newName;
	



	public String getNewName() {
		return newName;
	}

	public void setNewname(String newName) {
		this.newName = newName;
	}

	
}
