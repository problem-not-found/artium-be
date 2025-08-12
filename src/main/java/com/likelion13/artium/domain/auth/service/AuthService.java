/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.service;

import com.likelion13.artium.global.exception.CustomException;
import jakarta.servlet.http.HttpServletResponse;

import com.likelion13.artium.domain.auth.dto.request.LoginRequest;
import com.likelion13.artium.domain.auth.dto.response.TokenResponse;

/**
 * 인증 서비스 인터페이스
 *
 * 사용자 인증과 관련된 기능을 정의합니다.
 */
public interface AuthService {

  /**
   * 로그인 메서드
   *
   * 사용자가 입력한 아이디와 비밀번호로 인증을 시도하고, 성공 시 JWT Access Token과 Refresh Token을 발급합니다.
   * 발급된 토큰 정보는 TokenResponse 객체로 반환됩니다.
   *
   * @param response HTTP 응답 객체. 필요 시 토큰을 헤더에 추가하는데 사용됩니다.
   * @param loginRequest 로그인 요청 데이터로 사용자 이름과 비밀번호를 포함합니다.
   * @return 로그인 성공 시 발급된 토큰 정보를 포함하는 TokenResponse 객체
   * @throws CustomException 로그인 실패 또는 사용자 조회 실패 시 예외를 발생시킵니다.
   */
  TokenResponse login(LoginRequest loginRequest);

  /**
   * 로그아웃 메서드
   *
   * 전달받은 Access Token을 기반으로 사용자를 식별하고,
   * 해당 사용자의 Refresh Token을 Redis에서 삭제하며,
   * Access Token을 블랙리스트에 등록하여 더 이상 사용할 수 없도록 만듭니다.
   *
   * @param accessToken 로그아웃 대상 사용자의 Access Token 문자열
   * @return 로그아웃 성공 메시지 문자열
   * @throws CustomException 로그아웃 처리 실패 시 예외를 발생시킵니다.
   */
  String logout(String accessToken);

}
