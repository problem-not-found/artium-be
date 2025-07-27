/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.security;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(request);
    Map<String, Object> attributes = oauth2User.getAttributes();
    String provider = request.getClientRegistration().getRegistrationId();
    String email, nickname;

    switch (provider) {
      case "google" -> {
        email = (String) attributes.get("email");
        nickname = (String) attributes.get("name");
      }
      case "kakao" -> {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        email = (String) kakaoAccount.get("email");
        nickname = (String) profile.get("nickname");
      }
      case "naver" -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        email = (String) response.get("email");
        nickname = (String) response.get("nickname");
      }
      default -> throw new OAuth2AuthenticationException("Unknown provider: " + provider);
    }

    userRepository
        .findByUsername(email)
        .orElseGet(() -> userRepository.save(User.fromOAuth(email, provider, nickname)));

    String nameAttributeKey =
        switch (provider) {
          case "google" -> "email";
          case "kakao" -> "id";
          case "naver" -> "resultcode";
          default -> throw new OAuth2AuthenticationException("Unknown provider: " + provider);
        };

    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
        attributes,
        nameAttributeKey);
  }
}
