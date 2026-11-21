package com.dyno.service;

import com.dyno.dto.AdminLoginRequest;
import com.dyno.dto.AdminLoginResponse;

public interface AdminAuthService {

    AdminLoginResponse login(AdminLoginRequest request);
}