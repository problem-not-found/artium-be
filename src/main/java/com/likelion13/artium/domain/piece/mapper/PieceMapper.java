/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.piece.dto.response.PieceFeedResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceDetail.mapper.PieceDetailMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PieceMapper {

  private final PieceDetailMapper pieceDetailMapper;

  public PieceResponse toPieceResponse(Piece piece) {
    return toPieceResponseWithLike(piece, null);
  }

  public PieceResponse toPieceResponseWithLike(Piece piece, Boolean isLike) {
    return buildBasePieceResponse(piece).isLike(isLike).build();
  }

  private PieceResponse.PieceResponseBuilder buildBasePieceResponse(Piece piece) {
    return PieceResponse.builder()
        .pieceId(piece.getId())
        .title(piece.getTitle())
        .description(piece.getDescription())
        .imageUrl(piece.getImageUrl())
        .isPurchasable(piece.getIsPurchasable())
        .saveStatus(piece.getSaveStatus())
        .progressStatus(piece.getProgressStatus())
        .userId(piece.getUser().getId())
        .pieceDetails(
            piece.getPieceDetails() == null
                ? List.of()
                : piece.getPieceDetails().stream()
                    .map(pieceDetailMapper::toPieceDetailSummaryResponse)
                    .toList());
  }

  public PieceSummaryResponse toPieceSummaryResponse(Piece piece) {
    return PieceSummaryResponse.builder()
        .pieceId(piece.getId())
        .title(piece.getTitle())
        .description(piece.getDescription())
        .imageUrl(piece.getImageUrl())
        .isPurchasable(piece.getIsPurchasable())
        .saveStatus(piece.getSaveStatus())
        .progressStatus(piece.getProgressStatus())
        .userId(piece.getUser().getId())
        .build();
  }

  public PieceFeedResponse toPieceFeedResponse(Piece piece, Boolean isLike) {
    return PieceFeedResponse.builder()
        .pieceId(piece.getId())
        .title(piece.getTitle())
        .imageUrl(piece.getImageUrl())
        .isLike(isLike)
        .build();
  }
}
