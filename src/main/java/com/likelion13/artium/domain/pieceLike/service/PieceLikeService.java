/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.service;

import com.likelion13.artium.domain.pieceLike.dto.response.PieceLikeResponse;

public interface PieceLikeService {

  /**
   * @param pieceId 작품 식별자
   * @return 작품 좋아요 정보 응답
   */
  PieceLikeResponse likePiece(Long pieceId);

  /**
   * @param pieceId 작품 식별자
   * @return 작품 좋아요 정보 응답
   */
  PieceLikeResponse unlikePiece(Long pieceId);
}
