/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
  LOGIN_FAIL("AUTH_4001", "로그인 처리 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST),
  TOKEN_FAIL("AUTH_4002", "액세스 토큰 요청에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
  USER_INFO_FAIL("AUTH_4003", "사용자 정보 요청에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
  INVALID_ACCESS_TOKEN("AUTH_4004", "유효하지 않은 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
  ACCESS_TOKEN_EXPIRED("AUTH_4005", "액세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_REQUIRED("AUTH_4006", "리프레시 토큰이 필요합니다.", HttpStatus.FORBIDDEN),
  REFRESH_TOKEN_NOT_FOUND("AUTH_4007", "리프레시 토큰을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  JWT_TOKEN_EXPIRED("JWT_4001", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
  UNSUPPORTED_TOKEN("JWT_4002", "지원되지 않는 JWT 형식입니다.", HttpStatus.UNAUTHORIZED),
  MALFORMED_JWT_TOKEN("JWT_4003", "JWT 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
  INVALID_SIGNATURE("JWT_4004", "JWT 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
  ILLEGAL_ARGUMENT("JWT_4005", "JWT 토큰 값이 잘못되었습니다.", HttpStatus.UNAUTHORIZED);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
