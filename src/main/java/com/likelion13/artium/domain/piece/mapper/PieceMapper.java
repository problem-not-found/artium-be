/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;

@Component
public class PieceMapper {

  public PieceResponse toPieceResponse(Piece piece) {
    return PieceResponse.builder()
        .pieceId(piece.getId())
        .title(piece.getTitle())
        .description(piece.getDescription())
        .imageUrl(piece.getImageUrl())
        .isPurchasable(piece.getIsPurchasable())
        .status(piece.getStatus())
        .userId(piece.getUser().getId())
        .pieceDetails(
            piece.getPieceDetails() == null
                ? List.of()
                : piece.getPieceDetails().stream().map(PieceDetail::getImageUrl).toList())
        .build();
  }

  public PieceSummaryResponse toPieceSummaryResponse(Piece piece) {
    return PieceSummaryResponse.builder()
        .pieceId(piece.getId())
        .title(piece.getTitle())
        .description(piece.getDescription())
        .imageUrl(piece.getImageUrl())
        .isPurchasable(piece.getIsPurchasable())
        .status(piece.getStatus())
        .userId(piece.getUser().getId())
        .build();
  }

  public PieceResponse toPieceResponseWithDetail(
      PieceResponse pieceResponse, List<String> detailImageUrls) {
    return PieceResponse.builder()
        .pieceId(pieceResponse.getPieceId())
        .title(pieceResponse.getTitle())
        .description(pieceResponse.getDescription())
        .imageUrl(pieceResponse.getImageUrl())
        .isPurchasable(pieceResponse.getIsPurchasable())
        .status(pieceResponse.getStatus())
        .userId(pieceResponse.getUserId())
        .pieceDetails(detailImageUrls)
        .build();
  }
}
