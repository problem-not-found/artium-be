package com.likelion13.artium.domain.user.service;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.entity.Role;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.mapper.UserMapper;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.exception.CustomException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public SignUpResponse signUp(SignUpRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
    }

    // 비밀번호 인코딩
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // 유저 엔티티 생성
    User user =
        User.builder()
            .username(request.getUsername())
            .password(encodedPassword)
            .nickname(request.getNickname())
            .provider("custom")
            .role(Role.USER)
            .build();

    User savedUser = userRepository.save(user);
    log.info("새로운 사용자 생성: {}", savedUser.getUsername());

    return userMapper.toSignUpResponse(savedUser);
  }

  @Override
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.error("인증 실패 - 인증 정보 없음");
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    Object principal = authentication.getPrincipal();
    String username = "";

    try {
      if (principal instanceof OAuth2User oauthUser) {
        Object email = oauthUser.getAttribute("email");
        if (email != null) {
          username = (String) email;
        } else {
          Map<String, Object> kakaoAccount = oauthUser.getAttribute("kakao_account");
          if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            username = (String) kakaoAccount.get("email");
          }
        }
      } else if (principal instanceof String str) {
        username = str;
      } else if (principal instanceof UserDetails userDetails) {
        username = userDetails.getUsername();
      } else {
        log.error("인증 실패 - Principal 타입 알 수 없음: {}", principal.getClass());
        throw new CustomException(UserErrorCode.UNAUTHORIZED);
      }
    } catch (Exception e) {
      log.error("인증 정보 추출 중 오류", e);
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    if (username == null || username.isBlank()) {
      log.error("인증 실패 - 추출된 username이 null 또는 빈 문자열");
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    log.debug("JWT에서 추출한 email: {}", username);

    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }
}
