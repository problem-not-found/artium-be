/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {
  REVIEW_NOT_FOUND("REVIEW_4003", "감상평을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  REVIEW_ACCESS_DENIED("REVIEW_4004", "해당 감상평에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
  REVIEW_API_ERROR("REVIEW_5001", "감상평 API 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
