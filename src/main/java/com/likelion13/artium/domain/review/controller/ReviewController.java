/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.likelion13.artium.domain.review.dto.request.ReviewRequest;
import com.likelion13.artium.domain.review.dto.response.ReviewResponse;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "감상평", description = "감상평 관련 API")
@RequestMapping("/api/exhibitions/{exhibition-id}/reviews")
public interface ReviewController {

  @PostMapping
  @Operation(summary = "감상평 작성", description = "좋아요 할 사용자의 식별자를 요청 받아 좋아요를 등록합니다.")
  ResponseEntity<BaseResponse<ReviewResponse>> createReview(
      @Parameter(description = "감상평을 작성할 전시 식별자", example = "1")
          @PathVariable(name = "exhibition-id")
          Long exhibitionId,
      @Parameter(description = "감상평 작성 요청 정보") @Valid @RequestBody ReviewRequest request);

  @GetMapping
  @Operation(summary = "감상평 조회", description = "전시 식별자에 대한 감상평 리스트를 조회합니다.")
  ResponseEntity<BaseResponse<List<ReviewResponse>>> getReviewByExhibitionId(
      @Parameter(description = "감상평을 조회할 전시 식별자", example = "1")
          @PathVariable(name = "exhibition-id")
          Long exhibitionId);

  @PutMapping("/{review-id}")
  @Operation(summary = "감상평 수정", description = "감상평 식별자를 요청 받아 사용자가 작성했던 감상평을 수정합니다.")
  ResponseEntity<BaseResponse<ReviewResponse>> updateReview(
      @Parameter(description = "감상평을 수정할 전시 식별자", example = "1")
          @PathVariable(name = "exhibition-id")
          Long exhibitionId,
      @Parameter(description = "수정할 감상평 식별자", example = "1") @PathVariable(name = "review-id")
          Long reviewId,
      @Parameter(description = "감상평 수정 요청 정보") @Valid @RequestBody ReviewRequest request);

  @DeleteMapping("/{review-id}")
  @Operation(summary = "감상평 삭제", description = "감상평 식별자를 요청 받아 사용자가 작성했던 감상평을 삭제합니다.")
  ResponseEntity<BaseResponse<String>> deleteReview(
      @Parameter(description = "감상평을 삭제할 전시 식별자", example = "1")
          @PathVariable(name = "exhibition-id")
          Long exhibitionId,
      @Parameter(description = "삭제할 감상평 식별자", example = "1") @PathVariable(name = "review-id")
          Long reviewId);
}
