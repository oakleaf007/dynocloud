package com.dyno.service;

import com.dyno.dto.LoginReq;
import com.dyno.dto.LoginResponse;
import com.dyno.dto.RegisterReq;

public interface AuthService{
	void register(RegisterReq request);
	LoginResponse login(LoginReq request);
}
