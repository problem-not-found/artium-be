/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceFeedResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.RecommendSortBy;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.service.PieceService;
import com.likelion13.artium.domain.pieceLike.dto.response.PieceLikeResponse;
import com.likelion13.artium.domain.pieceLike.service.PieceLikeService;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.exception.PageErrorStatus;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PieceControllerImpl implements PieceController {

  private final PieceService pieceService;
  private final PieceLikeService pieceLikeService;

  @Override
  public ResponseEntity<BaseResponse<PieceSummaryResponse>> createPiece(
      @RequestParam SaveStatus saveStatus,
      @RequestPart("data") CreatePieceRequest createPieceRequest,
      @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages) {

    if (detailImages != null && detailImages.size() > 5)
      throw new CustomException(PieceErrorCode.TOO_MANY_DETAIL_IMAGES);

    if (saveStatus == SaveStatus.APPLICATION) {
      if (!validateCreatePieceFields(createPieceRequest, mainImage)) {
        throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
      }
    } else if (saveStatus == SaveStatus.DRAFT) {
      if (!validateDraftPieceFields(createPieceRequest, mainImage)) {
        throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
      }
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.success(
                pieceService.createPiece(createPieceRequest, saveStatus, mainImage, detailImages)));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceLikeResponse>> likePiece(
      @PathVariable(value = "piece-id") Long pieceId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success(pieceLikeService.likePiece(pieceId)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<PieceFeedResponse>>> getPiecePageByType(
      @RequestParam SortBy sortBy, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
    Pageable pageable = validatePageable(pageNum, pageSize);
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.getPiecePageByType(sortBy, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<PieceSummaryResponse>>> getUserPiecePage(
      @RequestParam Long userId, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.getPiecePage(userId, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<PieceSummaryResponse>>> getMyPieces(
      @RequestParam Boolean applicated,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.getMyPiecePage(applicated, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<PieceSummaryResponse>>> getLikePieces(
      @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.getLikePieces(pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<PieceFeedResponse>>> getRecommendationPieces(
      @RequestParam RecommendSortBy sortBy,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {
    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.getRecommendationPiecePage(sortBy, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceResponse>> getPiece(
      @PathVariable(value = "piece-id") Long pieceId) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.getPiece(pieceId)));
  }

  @Override
  public ResponseEntity<BaseResponse<Integer>> getPieceDraftCount() {

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.getPieceDraftCount()));
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

    if (saveStatus == SaveStatus.DRAFT) {
      if (!validateDraftPieceFields(updatePieceRequest, mainImage)) {
        throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
      }
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                pieceService.updatePiece(
                    pieceId, updatePieceRequest, saveStatus, mainImage, detailImages)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deletePieces(@RequestParam List<Long> pieceIds) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceService.deletePieces(pieceIds)));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceLikeResponse>> unlikePiece(
      @PathVariable(value = "piece-id") Long pieceId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(pieceLikeService.unlikePiece(pieceId)));
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

  private boolean validateCreatePieceFields(
      CreatePieceRequest createPieceRequest, MultipartFile mainImage) {
    return (createPieceRequest.getTitle() != null && !createPieceRequest.getTitle().isEmpty())
        && (createPieceRequest.getDescription() != null
            && !createPieceRequest.getDescription().isEmpty())
        && createPieceRequest.getIsPurchasable() != null
        && (mainImage != null && !mainImage.isEmpty());
  }

  private boolean validateDraftPieceFields(
      CreatePieceRequest createPieceRequest, MultipartFile mainImage) {
    return !((createPieceRequest.getTitle() == null || createPieceRequest.getTitle().isEmpty())
        && (createPieceRequest.getDescription() == null
            || createPieceRequest.getDescription().isEmpty())
        && (mainImage == null || mainImage.isEmpty()));
  }

  private boolean validateDraftPieceFields(
      UpdatePieceRequest updatePieceRequest, MultipartFile mainImage) {
    return !((updatePieceRequest.getTitle() == null || updatePieceRequest.getTitle().isEmpty())
        && (updatePieceRequest.getDescription() == null
            || updatePieceRequest.getDescription().isEmpty())
        && (mainImage == null || mainImage.isEmpty()));
  }
}
