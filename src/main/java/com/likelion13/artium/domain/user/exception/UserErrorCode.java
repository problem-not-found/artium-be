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
  CODE_ALREADY_EXISTS("USER_4002", "이미 존재하는 사용자 코드입니다.", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND("USER_4003", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  UNAUTHORIZED("USER_4004", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
  CANNOT_LIKE_SELF("USER_4005", "자기 자신은 좋아요 할 수 없습니다.", HttpStatus.BAD_REQUEST),
  ALREADY_LIKED("USER_4006", "이미 좋아요한 사용자입니다.", HttpStatus.BAD_REQUEST),
  LIKE_NOT_FOUND("USER_4007", "좋아요 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INVALID_INPUT_REQUEST("USER_4008", "유효하지 않은 입력 값을 포함한 요청입니다.", HttpStatus.BAD_REQUEST),
  FORBIDDEN("USER_4009", "허가되지 않은 접근입니다.", HttpStatus.FORBIDDEN),
  INVALID_SORT_TYPE("USER_4010", "유효하지 않은 정렬 요청입니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}
