/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.ai.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmbeddingErrorCode implements BaseErrorCode {
  EMBEDDING_ILLEGAL_ARGUMENT("EMBEDDING_4001", "유효하지 않은 임베딩 요청 값입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
