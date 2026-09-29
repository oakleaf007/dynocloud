package com.dyno.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.dyno.entity.File;
import com.dyno.entity.User;

public interface FileService {

	File upload(MultipartFile file,
				User user) throws IOException;

}
