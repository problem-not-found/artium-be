/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.qdrant.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QdrantErrorCode implements BaseErrorCode {
  VECTOR_SIZE_MISMATCH("QDRANT_5001", "벡터의 크기가 알맞지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  QDRANT_RETRIEVE_FAILD("QDRANT_5002", "벡터값 불러오기에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  QDRANT_REQUEST_FAILED("QDRANT_5003", "QDRANT 요청에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}
