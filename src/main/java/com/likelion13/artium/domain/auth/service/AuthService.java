/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.service;

import com.likelion13.artium.domain.auth.dto.request.LoginRequest;
import com.likelion13.artium.domain.auth.dto.response.TokenResponse;
import com.likelion13.artium.global.exception.CustomException;

/**
 * 인증 서비스 인터페이스
 *
 * <p>사용자 인증과 관련된 주요 기능들을 제공합니다.
 *
 * <p>주요 기능:
 *
 * <ul>
 *   <li>사용자 로그인
 *   <li>사용자 로그아웃
 * </ul>
 */
public interface AuthService {

  /**
   * 사용자의 아이디와 비밀번호를 검증하여 인증을 수행하고, 성공 시 JWT Access Token과 Refresh Token을 발급합니다.
   *
   * @param loginRequest 로그인 요청 정보(아이디, 비밀번호 포함)
   * @return 발급된 Access Token과 Refresh Token 정보를 담은 {@link TokenResponse} 객체
   * @throws CustomException 로그인 실패 또는 사용자 조회 실패 시 발생
   */
  TokenResponse login(LoginRequest loginRequest);

  /**
   * Access Token을 기반으로 사용자를 로그아웃 처리합니다.
   *
   * <p>로그아웃 시 해당 사용자의 Refresh Token을 Redis에서 삭제하고, Access Token을 블랙리스트에 등록하여 재사용을 방지합니다.
   *
   * @param accessToken 로그아웃 대상 사용자의 Access Token 문자열
   * @return 로그아웃 성공 메시지 문자열
   * @throws CustomException 로그아웃 처리 실패 시 발생
   */
  String logout(String accessToken);
}
