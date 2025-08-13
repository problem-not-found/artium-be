/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.service.PieceService;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.exception.PageErrorStatus;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PieceControllerImpl implements PieceController {

  private final PieceService pieceService;

  @Override
  public ResponseEntity<BaseResponse<PageResponse<PieceSummaryResponse>>> getPieces(
      @RequestParam Long userId, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.ok(
        BaseResponse.success(
            200, "작품 리스트 조회에 성공했습니다.", pieceService.getPiecePage(userId, pageable)));
  }

  @Operation(summary = "내 작품 리스트 조회 API", description = "내 작품 리스트를 조회하기 위한 API")
  @GetMapping("/my-page")
  public ResponseEntity<BaseResponse<PageResponse<PieceSummaryResponse>>> getMyPieces(
      @RequestParam Boolean applicated,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.ok(
        BaseResponse.success(
            200, "내 작품 리스트 조회에 성공했습니다.", pieceService.getMyPiecePage(applicated, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceSummaryResponse>> createPiece(
      @RequestParam SaveStatus saveStatus,
      @RequestPart("data") CreatePieceRequest createPieceRequest,
      @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages) {

    if (detailImages != null && detailImages.size() > 5)
      throw new CustomException(PieceErrorCode.TOO_MANY_DETAIL_IMAGES);
    PieceSummaryResponse pieceSummaryResponse =
        pieceService.createPiece(createPieceRequest, saveStatus, mainImage, detailImages);

    return ResponseEntity.ok(BaseResponse.success(201, "작품 등록에 성공했습니다.", pieceSummaryResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceResponse>> getPiece(
      @PathVariable(value = "piece-id") Long pieceId) {

    PieceResponse pieceResponse = pieceService.getPiece(pieceId);

    return ResponseEntity.ok(BaseResponse.success(200, "작품 조회에 성공했습니다.", pieceResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceResponse>> updatePiece(
      @PathVariable(value = "piece-id") Long pieceId,
      @RequestParam SaveStatus saveStatus,
      @RequestPart("data") UpdatePieceRequest updatePieceRequest,
      @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages) {

    int remainCount =
        updatePieceRequest.getRemainPieceDetailIds() == null
            ? 0
            : updatePieceRequest.getRemainPieceDetailIds().size();
    if (detailImages != null && remainCount + detailImages.size() > 5)
      throw new CustomException(PieceErrorCode.TOO_MANY_DETAIL_IMAGES);
    PieceResponse pieceResponse =
        pieceService.updatePiece(pieceId, updatePieceRequest, saveStatus, mainImage, detailImages);

    return ResponseEntity.ok(BaseResponse.success(200, "작품 수정에 성공했습니다.", pieceResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deletePiece(
      @PathVariable(value = "piece-id") Long pieceId) {

    pieceService.deletePiece(pieceId);

    return ResponseEntity.ok(BaseResponse.success(pieceId + "번 식별자 작품이 정상적으로 삭제되었습니다."));
  }

  @Override
  public ResponseEntity<BaseResponse<Integer>> getPieceDraftCount() {

    return ResponseEntity.ok(
        BaseResponse.success(200, "임시저장 작품 개수 조회에 성공했습니다.", pieceService.getPieceDraftCount()));
  }

  private Pageable validatePageable(Integer pageNum, Integer pageSize) {
    if (pageNum < 1) {
      throw new CustomException(PageErrorStatus.PAGE_NOT_FOUND);
    }
    if (pageSize < 1) {
      throw new CustomException(PageErrorStatus.PAGE_SIZE_ERROR);
    }

    return PageRequest.of(pageNum - 1, pageSize);
  }
}
