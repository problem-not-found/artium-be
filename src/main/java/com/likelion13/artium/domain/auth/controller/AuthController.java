/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13.artium.domain.auth.dto.request.LoginRequest;
import com.likelion13.artium.domain.auth.dto.response.TokenResponse;
import com.likelion13.artium.domain.auth.service.AuthService;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.jwt.JwtProvider;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auths")
@Tag(name = "Auth", description = "Auth 관리 API")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;

  @Operation(summary = "로그인", description = "사용자 로그인을 위한 API")
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<String>> login(
      HttpServletResponse response, @RequestBody @Valid LoginRequest loginRequest) {

    TokenResponse tokenResponse = authService.login(response, loginRequest);

    jwtProvider.addJwtToCookie(
        response,
        tokenResponse.getRefreshToken(),
        "REFRESH_TOKEN",
        jwtProvider.getExpirationTime(tokenResponse.getRefreshToken()));
    jwtProvider.addJwtToCookie(
        response,
        tokenResponse.getAccessToken(),
        "ACCESS_TOKEN",
        jwtProvider.getExpirationTime(tokenResponse.getAccessToken()));

    return ResponseEntity.ok(BaseResponse.success(tokenResponse.getUsername()));
  }

  @Operation(summary = "로그아웃", description = "사용자 로그아웃을 위한 API (Redis RT 삭제 + 액세스 토큰 블랙리스트 처리)")
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<String>> logout(
      @RequestHeader("Authorization") String header) {
    String accessToken = header.replace("Bearer ", "");
    authService.logout(accessToken);
    return ResponseEntity.ok(BaseResponse.success("로그아웃에 성공했습니다.", null));
  }
}
