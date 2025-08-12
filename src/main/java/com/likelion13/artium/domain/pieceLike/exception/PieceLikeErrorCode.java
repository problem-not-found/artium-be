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
  NOT_LIKED("PIECE_LIKE_4092", "좋아요한 상태가 아닙니다.", HttpStatus.CONFLICT);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
