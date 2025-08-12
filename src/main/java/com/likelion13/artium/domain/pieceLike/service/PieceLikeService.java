/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.service;

import com.likelion13.artium.domain.pieceLike.dto.response.PieceLikeResponse;
import com.likelion13.artium.global.security.CustomUserDetails;

public interface PieceLikeService {

  /**
   * @param userDetails 요청 유저 정보
   * @param pieceId 작품 식별자
   * @return 작품 좋아요 정보 응답
   */
  PieceLikeResponse likePiece(CustomUserDetails userDetails, Long pieceId);

  /**
   * @param userDetails 요청 유저 정보
   * @param pieceId 작품 식별자
   * @return 작품 좋아요 정보 응답
   */
  PieceLikeResponse unlikePiece(CustomUserDetails userDetails, Long pieceId);
}
