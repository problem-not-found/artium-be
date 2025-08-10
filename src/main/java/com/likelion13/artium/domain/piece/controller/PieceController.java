/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.service.PieceService;
import com.likelion13.artium.domain.pieceDetail.dto.response.PieceDetailResponse;
import com.likelion13.artium.domain.pieceDetail.service.PieceDetailService;
import com.likelion13.artium.global.response.BaseResponse;
import com.likelion13.artium.global.s3.dto.S3Response;
import com.likelion13.artium.global.s3.entity.PathName;
import com.likelion13.artium.global.s3.service.S3Service;
import com.likelion13.artium.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pieces")
@Tag(name = "Piece", description = "Piece(작품) 관리 API")
public class PieceController {

  private final PieceService pieceService;
  private final PieceDetailService pieceDetailService;
  private final S3Service s3Service;

  @Operation(summary = "내 작품 리스트 조회 API", description = "전시장 메인 페이지에서 내 작품 리스트를 조회하기 위한 API")
  @GetMapping("/")
  public ResponseEntity<BaseResponse<List<PieceSummaryResponse>>> getPieces(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUser().getId();
    List<PieceSummaryResponse> pieceSummaryResponses = pieceService.getAllPieces(userId);

    return ResponseEntity.ok(BaseResponse.success("작품 리스트 조회에 성공했습니다.", pieceSummaryResponses));
  }

  @Operation(summary = "내 작품 정보 등록하기 API", description = "내 작품 정보 등록에서 작품을 등록하기 위한 API")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<PieceResponse>> createPiece(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "작품 등록 내용") @RequestPart("data") @Valid
          CreatePieceRequest createPieceRequest,
      @Parameter(description = "작품 메인 이미지") @RequestPart("image") MultipartFile image,
      @Parameter(description = "작품 디테일 컷 모음") @RequestPart(value = "detailImages", required = false)
          List<MultipartFile> detailImages) {

    Long userId = userDetails.getUser().getId();

    S3Response s3Response = s3Service.uploadImage(PathName.FOLDER1, image);
    String mainImageUrl = s3Response.getImageUrl();

    PieceResponse pieceResponse =
        pieceService.createPiece(userId, createPieceRequest, mainImageUrl);

    List<S3Response> s3Responses;
    List<String> detailImageUrls;
    List<PieceDetailResponse> pieceDetailResponses;

    if (detailImages != null && detailImages.size() > 0) {
      s3Responses =
          detailImages.stream()
              .map(detailImage -> s3Service.uploadImage(PathName.FOLDER2, detailImage))
              .toList();
      detailImageUrls = s3Responses.stream().map(S3Response::getImageUrl).toList();

      pieceDetailResponses = new ArrayList<>();
      for (String detailImageUrl : detailImageUrls) {
        PieceDetailResponse pieceDetailResponse =
            pieceDetailService.createPieceDetails(pieceResponse.getPieceId(), detailImageUrl);
        pieceDetailResponses.add(pieceDetailResponse);
      }
    }

    return ResponseEntity.ok(BaseResponse.success("작품 등록에 성공했습니다.", pieceResponse));
  }

  @Operation(
      summary = "특정 작품 정보 조회하기 API",
      description = "특정 작품 정보 수정 및 작품 상세 정보에서 작품 정보를 조회하기 위한 API")
  @GetMapping("/{piece-id}")
  public ResponseEntity<BaseResponse<PieceResponse>> getPiece(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId) {

    Long userId = userDetails.getUser().getId();
    PieceResponse pieceResponse = pieceService.getPiece(userId, pieceId);

    return ResponseEntity.ok(BaseResponse.success("작품 조회에 성공했습니다.", pieceResponse));
  }

  @Operation(summary = "특정 작품 정보 수정하기 API", description = "특정 작품 정보 수정에서 작품 정보를 수정하기 위한 API")
  @PutMapping("/{piece-id}")
  public ResponseEntity<BaseResponse<PieceResponse>> updatePiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Parameter(description = "작품 수정 내용") @RequestPart("data") @Valid
          UpdatePieceRequest updatePieceRequest,
      @RequestPart(value = "image") MultipartFile image,
      @RequestPart(value = "image") List<MultipartFile> pieceDetails) {

    Long userId = userDetails.getUser().getId();

    PieceResponse pieceResponse = pieceService.updatePiece(userId, pieceId, updatePieceRequest);

    return ResponseEntity.ok(BaseResponse.success("작품 수정에 성공했습니다,", pieceResponse));
  }
}
