/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.service.PieceService;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.response.BaseResponse;
import com.likelion13.artium.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PieceControllerImpl implements PieceController {

  private final PieceService pieceService;

  @Override
  public ResponseEntity<BaseResponse<List<PieceSummaryResponse>>> getPieces(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    List<PieceSummaryResponse> pieceSummaryResponses = pieceService.getAllPieces(userDetails);

    return ResponseEntity.ok(
        BaseResponse.success(200, "작품 리스트 조회에 성공했습니다.", pieceSummaryResponses));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceSummaryResponse>> createPiece(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestPart("data") @Valid CreatePieceRequest createPieceRequest,
      @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages) {

    if (detailImages != null && detailImages.size() > 5)
      throw new CustomException(PieceErrorCode.TOO_MANY_DETAIL_IMAGES);
    PieceSummaryResponse pieceSummaryResponse =
        pieceService.createPiece(userDetails, createPieceRequest, mainImage, detailImages);

    return ResponseEntity.ok(BaseResponse.success(201, "작품 등록에 성공했습니다.", pieceSummaryResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceResponse>> getPiece(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable(value = "piece-id") Long pieceId) {

    PieceResponse pieceResponse = pieceService.getPiece(userDetails, pieceId);

    return ResponseEntity.ok(BaseResponse.success(200, "작품 조회에 성공했습니다.", pieceResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceResponse>> updatePiece(
      @PathVariable(value = "piece-id") Long pieceId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestPart("data") @Valid UpdatePieceRequest updatePieceRequest,
      @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages) {

    int remainCount =
        updatePieceRequest.getRemainPieceDetailIds() == null
            ? 0
            : updatePieceRequest.getRemainPieceDetailIds().size();
    if (detailImages != null && remainCount + detailImages.size() > 5)
      throw new CustomException(PieceErrorCode.TOO_MANY_DETAIL_IMAGES);
    PieceResponse pieceResponse =
        pieceService.updatePiece(userDetails, pieceId, updatePieceRequest, mainImage, detailImages);

    return ResponseEntity.ok(BaseResponse.success(200, "작품 수정에 성공했습니다.", pieceResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deletePiece(
      @PathVariable(value = "piece-id") Long pieceId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    pieceService.deletePiece(userDetails, pieceId);

    return ResponseEntity.ok(BaseResponse.success(pieceId + "번 식별자 작품이 정상적으로 삭제되었습니다."));
  }
}
