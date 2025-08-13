/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/pieces")
@Tag(name = "Piece", description = "Piece(작품) 관리 API")
public interface PieceController {

  @Operation(summary = "특정 유저 작품 리스트 조회 API", description = "특정 유저 작품 리스트를 조회하기 위한 API")
  @GetMapping()
  ResponseEntity<BaseResponse<PageResponse<PieceSummaryResponse>>> getPieces(
      @Parameter(description = "유저 식별자") @RequestParam Long userId,
      @Parameter(description = "페이지 번호", example = "1") @RequestParam Integer pageNum,
      @Parameter(description = "페이지 크기", example = "3") @RequestParam Integer pageSize);

  @Operation(summary = "내 작품 리스트 조회 API", description = "내 작품 리스트를 조회하기 위한 API")
  @GetMapping("/my-page")
  ResponseEntity<BaseResponse<PageResponse<PieceSummaryResponse>>> getMyPieces(
      @Parameter(description = "등록 신청 여부", example = "true") @RequestParam Boolean applicated,
      @Parameter(description = "페이지 번호", example = "1") @RequestParam Integer pageNum,
      @Parameter(description = "페이지 크기", example = "3") @RequestParam Integer pageSize);

  @Operation(summary = "내 작품 정보 등록하기 API (로그인 필요)", description = "내 작품 정보 등록에서 작품을 등록하기 위한 API")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<PieceSummaryResponse>> createPiece(
      @Parameter(description = "작품 저장 상태", example = "APPLICATION") @RequestParam
          SaveStatus saveStatus,
      @Parameter(description = "작품 등록 내용") @RequestPart("data")
          CreatePieceRequest createPieceRequest,
      @Parameter(description = "작품 메인 이미지") @RequestPart(value = "mainImage", required = false)
          MultipartFile mainImage,
      @Parameter(description = "작품 디테일 컷 모음") @RequestPart(value = "detailImages", required = false)
          List<MultipartFile> detailImages);

  @Operation(
      summary = "특정 작품 정보 조회하기 API",
      description = "특정 작품 정보 수정 및 작품 상세 정보에서 작품 정보를 조회하기 위한 API")
  @GetMapping("/{piece-id}")
  ResponseEntity<BaseResponse<PieceResponse>> getPiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId);

  @Operation(summary = "특정 작품 정보 수정하기 API", description = "특정 작품 정보 수정에서 작품 정보를 수정하기 위한 API")
  @PutMapping(value = "/{piece-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<PieceResponse>> updatePiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId,
      @Parameter(description = "작품 상태", example = "UNREGISTERED") @RequestParam
          SaveStatus saveStatus,
      @Parameter(description = "작품 수정 내용") @RequestPart("data")
          UpdatePieceRequest updatePieceRequest,
      @Parameter(description = "작품 메인 이미지") @RequestPart(value = "mainImage", required = false)
          MultipartFile mainImage,
      @Parameter(description = "작품 디테일 컷 모음") @RequestPart(value = "detailImages", required = false)
          List<MultipartFile> detailImages);

  @Operation(summary = "특정 작품 삭제하기 API", description = "특정 작품 정보 또는 작품 임시저장에서 작품을 삭제하기 위한 API")
  @DeleteMapping("/{piece-id}")
  ResponseEntity<BaseResponse<String>> deletePiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId);

  @Operation(summary = "임시저장된 작품 개수 조회", description = "사용자가 임시 저장한 작품의 총 개수를 반환하기 위한 API")
  @GetMapping("/draft-count")
  ResponseEntity<BaseResponse<Integer>> getPieceDraftCount();
}
