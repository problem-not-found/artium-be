/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.security;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.auth.dto.response.TokenResponse;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String provider =
        ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
    String email = null;

    switch (provider) {
      case "kakao":
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        if (kakaoAccount != null) {
          email = (String) kakaoAccount.get("email");
        }
        break;
      case "naver":
        Map<String, Object> naverResponse = oAuth2User.getAttribute("response");
        if (naverResponse != null) {
          email = (String) naverResponse.get("email");
        }
        break;
      case "google":
        email = oAuth2User.getAttribute("email");
        break;
      default:
        throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }

    if (email == null) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }

    User user =
        userRepository
            .findByUsername(email)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    TokenResponse tokenResponse = jwtProvider.createTokens(authentication);

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

    log.info("카카오 로그인 성공: {}", user.getUsername());

    response.addHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
    response.sendRedirect("/swagger-ui/index.html#/");
  }
}
