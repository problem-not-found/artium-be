/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.service;

import jakarta.servlet.http.HttpServletResponse;

import com.likelion13.artium.domain.auth.dto.request.LoginRequest;
import com.likelion13.artium.domain.auth.dto.response.TokenResponse;

public interface AuthService {

  TokenResponse login(HttpServletResponse response, LoginRequest loginRequest);

  String logout(String accessToken);
}
