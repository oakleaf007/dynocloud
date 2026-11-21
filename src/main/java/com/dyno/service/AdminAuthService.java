package com.dyno.service;

import com.dyno.dto.admin.AdminLoginRequest;
import com.dyno.dto.admin.AdminLoginResponse;

public interface AdminAuthService {

    AdminLoginResponse login(AdminLoginRequest request);
}