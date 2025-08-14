/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13.artium.domain.pieceLike.dto.response.PieceLikeResponse;
import com.likelion13.artium.domain.pieceLike.service.PieceLikeService;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PieceLikeControllerImpl implements PieceLikeController {

  private final PieceLikeService pieceLikeService;

  @Override
  public ResponseEntity<BaseResponse<PieceLikeResponse>> likePiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId) {

    return ResponseEntity.status(201)
        .body(BaseResponse.success(pieceLikeService.likePiece(pieceId)));
  }

  @Override
  public ResponseEntity<BaseResponse<PieceLikeResponse>> unlikePiece(
      @Parameter(description = "특정 작품 ID") @PathVariable(value = "piece-id") Long pieceId) {

    return ResponseEntity.status(204)
        .body(BaseResponse.success(pieceLikeService.unlikePiece(pieceId)));
  }
}
