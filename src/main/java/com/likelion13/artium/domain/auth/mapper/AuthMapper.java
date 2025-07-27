/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.auth.dto.response.LoginResponse;
import com.likelion13.artium.domain.user.entity.User;

@Component
public class AuthMapper {

  public LoginResponse toLoginResponse(User user, String accessToken, Long expirationTime) {
    return LoginResponse.builder()
        .accessToken(accessToken)
        .userId(user.getId())
        .username(user.getUsername())
        .role(user.getRole())
        .expirationTime(expirationTime)
        .build();
  }
}
