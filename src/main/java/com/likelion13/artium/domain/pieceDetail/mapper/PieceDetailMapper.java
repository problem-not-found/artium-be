/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceDetail.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.pieceDetail.dto.response.PieceDetailResponse;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;

@Component
public class PieceDetailMapper {

  public PieceDetailResponse toPieceDetailResponse(PieceDetail pieceDetail) {
    return PieceDetailResponse.builder()
        .pieceDetailId(pieceDetail.getId())
        .pieceId(pieceDetail.getPiece().getId())
        .imageUrl(pieceDetail.getImageUrl())
        .build();
  }
}
