package com.dyno.service;

import com.dyno.dto.LoginReq;
import com.dyno.dto.LoginResponse;
import com.dyno.dto.RegisterReq;
import com.dyno.dto.RegisterRes;

public interface AuthService{
	RegisterRes register(RegisterReq request);
	LoginResponse login(LoginReq request);
}
