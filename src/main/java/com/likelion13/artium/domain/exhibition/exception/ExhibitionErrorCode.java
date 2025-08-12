/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExhibitionErrorCode implements BaseErrorCode {
  INVALID_DATE_RANGE("EXHIBIT_4001", "유효하지 않은 날짜 요청입니다.", HttpStatus.BAD_REQUEST),
  EXHIBITION_API_ERROR("EXHIITB_5001", "전시 API 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
