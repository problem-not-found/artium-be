/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.likelion13.artium.domain.pieceLike.dto.response.PieceLikeResponse;
import com.likelion13.artium.global.response.BaseResponse;
import com.likelion13.artium.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/pieces")
@Tag(name = "Piece Like", description = "Piece Like(작품 좋아요) 관리 API")
public interface PieceLikeController {

  @Operation(summary = "특정 작품 좋아요 등록하기 API (로그인 필요)", description = "특정 작품 정보에서 좋아요를 등록하기 위한 API")
  @PostMapping("/{piece-id}/likes")
  ResponseEntity<BaseResponse<PieceLikeResponse>> likePiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId,
      @AuthenticationPrincipal CustomUserDetails userDetails);

  @Operation(summary = "특정 작품 좋아요 취소하기 API (로그인 필요)", description = "특정 작품 정보에서 좋아요를 취소하기 위한 API")
  @DeleteMapping("/{piece-id}/likes")
  ResponseEntity<BaseResponse<PieceLikeResponse>> unlikePiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId,
      @AuthenticationPrincipal CustomUserDetails userDetails);
}
