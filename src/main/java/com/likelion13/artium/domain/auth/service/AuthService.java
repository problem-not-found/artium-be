/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.service;

import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.auth.dto.request.LoginRequest;
import com.likelion13.artium.domain.auth.dto.response.TokenResponse;
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
  public TokenResponse login(HttpServletResponse response, LoginRequest loginRequest) {
    User user =
        userRepository
            .findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword());

    authenticationManager.authenticate(authenticationToken);

    TokenResponse tokenResponse = jwtProvider.createTokens(authenticationToken);

    log.info("로그인 성공: {}", user.getUsername());

    return tokenResponse;
  }

  public String getRefreshTokenFromRedis(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  @Transactional
  public void logout(String accessToken) {
    try {
      String username = jwtProvider.getUsernameFromToken(accessToken);
      String redisKey = "RT:" + username;
      Boolean result = redisTemplate.delete(redisKey);

      if (result) {
        log.info("Logout success: refresh token for '{}' deleted from Redis.", username);
      } else {
        log.warn("Logout attempted, but no refresh token found for '{}'.", username);
      }

      // 액세스 토큰 블랙리스트 처리
      long expiration = jwtProvider.getExpirationTime(accessToken);
      redisTemplate
          .opsForValue()
          .set("BL:" + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

      log.info("Access token for '{}' blacklisted until expiration.", username);
    } catch (Exception e) {
      log.error("Redis operation failed during logout for token: {}", accessToken, e);
      throw new RuntimeException("로그아웃 처리 중 오류가 발생했습니다.", e);
    }
  }
}
