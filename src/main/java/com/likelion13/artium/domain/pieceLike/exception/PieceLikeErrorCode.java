/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PieceLikeErrorCode implements BaseErrorCode {
  ALREADY_LIKED("PIECE_LIKE_4091", "이미 좋아요 상태입니다.", HttpStatus.CONFLICT),
  NOT_LIKED("PIECE_LIKE_4092", "좋아요한 상태가 아닙니다.", HttpStatus.CONFLICT),
  PIECE_LIKE_NOT_FOUND("PIECE_LIKE_4041", "좋아요 목록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  SELF_LIKE_NOT_ALLOWED("PIECE_LIKE_4001", "본인 작품은 좋아요 할 수 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_LIKE_REQUEST("PIECE_LIKE_4002", "유효하지 않은 좋아요 요청입니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}
