/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.auth.dto.request.LoginRequest;
import com.likelion13.artium.domain.auth.dto.response.TokenResponse;
import com.likelion13.artium.domain.auth.exception.AuthErrorCode;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.domain.user.service.UserService;
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
  private final UserService userService;
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
  public String logout(String accessToken) {
    String username = jwtProvider.getUsernameFromToken(accessToken);

    jwtProvider.deleteRefreshToken(username);
    jwtProvider.blacklistToken(accessToken);

    log.info("로그아웃 성공: {}", username);
    return "로그아웃 성공 - 사용자: " + username;
  }

  @Override
  public String reissueAccessToken(String refreshToken) {

    String username = jwtProvider.getUsernameFromToken(refreshToken);
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (!jwtProvider.validateRefreshToken(user.getUsername(), refreshToken)) {
      throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    log.info("AT 재발급 성공: {}", user.getUsername());
    return jwtProvider.createToken(authentication);
  }
}
