/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;

@Component
public class PieceSummaryMapper {

  public PieceSummaryResponse toPieceSummaryResponse(Piece piece) {
    return PieceSummaryResponse.builder()
        .pieceId(piece.getId())
        .title(piece.getTitle())
        .description(piece.getDescription())
        .imageUrl(piece.getImageUrl())
        .isPurchasable(piece.getIsPurchasable())
        .userId(piece.getUser().getId())
        .build();
  }
}
