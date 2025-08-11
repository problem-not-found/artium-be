/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.exception;

import org.springframework.http.HttpStatus;

import com.likelion13.artium.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PieceErrorCode implements BaseErrorCode {
  UNAUTHORIZED("PIECE_4031", "작품에 대한 접근 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
  PIECE_NOT_FOUND("PIECE_4041", "존재하지 않는 작품입니다.", HttpStatus.NOT_FOUND),
  TOO_MANY_DETAIL_IMAGES("PIECE_4001", "디테일 이미지는 최대 5개까지 업로드 가능합니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
