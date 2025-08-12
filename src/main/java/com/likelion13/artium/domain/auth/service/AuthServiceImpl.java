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
import com.likelion13.artium.domain.auth.exception.AuthErrorCode;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  @Transactional
  public TokenResponse login(LoginRequest loginRequest) {

    User user =
        userRepository
            .findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword());

    authenticationManager.authenticate(authenticationToken);

    try {
      TokenResponse tokenResponse = jwtProvider.createTokens(authenticationToken);

      log.info("로그인 성공: {}", user.getUsername());

      return tokenResponse;
    } catch (Exception e) {
      throw new CustomException(AuthErrorCode.LOGIN_FAIL);
    }
  }

  @Override
  @Transactional
  public String logout(String accessToken) {
    try {
      String username = jwtProvider.getUsernameFromToken(accessToken);
      String redisKey = "RT:" + username;
      Boolean result = redisTemplate.delete(redisKey);

      if (result) {
        log.info("로그아웃 성공: 사용자 '{}'의 리프레시 토큰이 레디스에서 삭제되었습니다.", username);
      } else {
        log.warn("로그아웃 시도 중 사용자 '{}'의 리프레시 토큰을 찾지 못 했습니다.", username);
      }

      // 액세스 토큰 블랙리스트 처리
      long expiration = jwtProvider.getExpirationTime(accessToken);
      redisTemplate
          .opsForValue()
          .set("BL:" + accessToken, "logout", expiration, TimeUnit.SECONDS);

      log.info("사용자 '{}'의 액세스 토큰이 만료까지 블랙리스트 처리되었습니다.", username);

      return "로그아웃 성공 - 사용자 아이디: " + username;
    } catch (Exception e) {
      throw new CustomException(AuthErrorCode.LOGOUT_FAIL);
    }
  }
}
