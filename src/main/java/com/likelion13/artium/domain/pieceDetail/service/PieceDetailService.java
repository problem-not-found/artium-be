/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceDetail.service;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;

public interface PieceDetailService {

  /**
   * @param piece 작품 객체
   * @param detailImageUrl 디테일 컷 Url
   * @return 디테일 컷 정보가 담긴 DTO
   */
  PieceDetail createPieceDetail(Piece piece, String detailImageUrl);
}
