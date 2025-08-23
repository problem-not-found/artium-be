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
  FORBIDDEN("PIECE_4031", "해당 작품에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
  PIECE_NOT_FOUND("PIECE_4041", "존재하지 않는 작품입니다.", HttpStatus.NOT_FOUND),
  TOO_MANY_DETAIL_IMAGES("PIECE_4001", "디테일 이미지는 최대 5개까지 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
  DETAIL_IMAGE_NOT_BELONG_TO_PIECE("PIECE_4002", "해당 작품의 디테일 이미지가 아닙니다.", HttpStatus.BAD_REQUEST),
  INVALID_APPLICATION("PIECE_4003", "유효하지 않은 작품 신청입니다.", HttpStatus.BAD_REQUEST),
  INVALID_SORT_TYPE("PIECE_4004", "유효하지 않은 정렬 요청입니다.", HttpStatus.BAD_REQUEST),
  ALREADY_REGISTERED_PIECE("PIECE_4005", "이미 등록된 작품입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
