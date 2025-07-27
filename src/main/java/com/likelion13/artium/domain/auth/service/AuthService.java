/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.auth.dto.request.LoginRequest;
import com.likelion13.artium.domain.auth.dto.response.LoginResponse;
import com.likelion13.artium.domain.auth.mapper.AuthMapper;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final AuthMapper authMapper;
  private final RedisTemplate<String, String> redisTemplate;

  @Transactional
  public LoginResponse login(LoginRequest loginRequest) {
    User user =
        userRepository
            .findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword());

    authenticationManager.authenticate(authenticationToken);

    String accessToken =
        jwtProvider.createAccessToken(user.getUsername(), user.getRole().toString(), "custom");
    String refreshToken =
        jwtProvider.createRefreshToken(user.getUsername(), UUID.randomUUID().toString());

    Long expirationTime = jwtProvider.getExpiration(accessToken);

    long refreshTokenExpiration = jwtProvider.getExpiration(refreshToken);
    redisTemplate
        .opsForValue()
        .set(
            "RT:" + user.getUsername(),
            refreshToken,
            refreshTokenExpiration,
            TimeUnit.MILLISECONDS);

    log.info("Custom login success: {}", user.getUsername());

    return authMapper.toLoginResponse(user, accessToken, expirationTime);
  }

  public String getRefreshTokenFromRedis(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  @Transactional
  public void logout(String accessToken) {
    String username = jwtProvider.getUsernameFromToken(accessToken);
    String redisKey = "RT:" + username;
    Boolean result = redisTemplate.delete(redisKey);

    if (result) {
      log.info("Logout success: refresh token for '{}' deleted from Redis.", username);
    } else {
      log.warn("Logout attempted, but no refresh token found for '{}'.", username);
    }

    // 액세스 토큰 블랙리스트 처리
    long expiration = jwtProvider.getExpiration(accessToken);
    redisTemplate
        .opsForValue()
        .set("BL:" + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    log.info("Access token for '{}' blacklisted until expiration.", username);
  }
}
