/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  USERNAME_ALREADY_EXISTS("USER_4001", "이미 존재하는 사용자 아이디입니다.", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND("USER_4002", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
  UNAUTHORIZED("USER_4003", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
