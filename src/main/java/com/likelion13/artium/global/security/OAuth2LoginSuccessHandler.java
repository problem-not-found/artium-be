/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.security;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
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
    Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");

    if (kakaoAccount == null || !kakaoAccount.containsKey("email")) {
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    String email = (String) kakaoAccount.get("email");

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

    boolean hasTheme = user.getThemePreferences() != null && !user.getThemePreferences().isEmpty();
    boolean hasMood = user.getMoodPreferences() != null && !user.getMoodPreferences().isEmpty();
    boolean hasFormat =
        user.getFormatPreferences() != null && !user.getFormatPreferences().isEmpty();

    boolean hasAge = user.getAge() != null;
    boolean hasGender = user.getGender() != null;
    boolean hasCode = user.getCode() != null && !user.getCode().isBlank();

    response.addHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
    if (hasAge && hasGender && hasCode && hasFormat && hasMood && hasTheme) {
      response.sendRedirect("https://artium.life");
    } else {
      response.sendRedirect("https://artiums.life/mytype");
    }
  }
}
