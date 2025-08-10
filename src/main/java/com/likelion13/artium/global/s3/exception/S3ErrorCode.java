/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.s3.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {
  FILE_NOT_FOUND("IMG4001", "존재하지 않는 이미지입니다.", HttpStatus.NOT_FOUND),
  FILE_SIZE_INVALID("IMG4002", "파일 크기는 5MB를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
  FILE_TYPE_INVALID("IMG4003", "이미지 파일만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
  FILE_URL_INVALID("IMG4004", "유효하지 않은 이미지 URL입니다.", HttpStatus.BAD_REQUEST),
  FILE_SERVER_ERROR("IMG5001", "이미지 처리 중 서버 에러, 관리자에게 문의 바랍니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
