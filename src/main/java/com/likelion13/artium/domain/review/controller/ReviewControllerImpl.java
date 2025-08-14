/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13.artium.domain.review.dto.request.ReviewRequest;
import com.likelion13.artium.domain.review.dto.response.ReviewResponse;
import com.likelion13.artium.domain.review.service.ReviewService;
import com.likelion13.artium.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {

  private final ReviewService reviewService;

  @Override
  public ResponseEntity<BaseResponse<ReviewResponse>> createReview(
      @PathVariable(name = "exhibition-id") Long exhibitionId,
      @Valid @RequestBody ReviewRequest request) {

    ReviewResponse reviewResponse = reviewService.createReview(exhibitionId, request);

    return ResponseEntity.status(201).body(BaseResponse.success(reviewResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<List<ReviewResponse>>> getReviewByExhibitionId(
      @PathVariable(name = "exhibition-id") Long exhibitionId) {

    return ResponseEntity.status(200)
        .body(BaseResponse.success(reviewService.getReviewByExhibitionId(exhibitionId)));
  }

  @Override
  public ResponseEntity<BaseResponse<ReviewResponse>> updateReview(
      @PathVariable(name = "exhibition-id") Long exhibitionId,
      @PathVariable(name = "review-id") Long reviewId,
      @Valid @RequestBody ReviewRequest request) {

    ReviewResponse reviewResponse = reviewService.updateReview(exhibitionId, reviewId, request);

    return ResponseEntity.status(200).body(BaseResponse.success(reviewResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deleteReview(
      @PathVariable(name = "exhibition-id") Long exhibitionId,
      @PathVariable(name = "review-id") Long reviewId) {

    return ResponseEntity.status(200)
        .body(BaseResponse.success(reviewService.deleteReview(exhibitionId, reviewId)));
  }
}
