/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13.artium.domain.review.dto.request.ReviewRequest;
import com.likelion13.artium.domain.review.dto.response.ReviewResponse;
import com.likelion13.artium.domain.review.service.ReviewService;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.exception.PageErrorStatus;
import com.likelion13.artium.global.page.response.PageResponse;
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
  public ResponseEntity<BaseResponse<PageResponse<ReviewResponse>>> getReviewByExhibitionId(
      @PathVariable(name = "exhibition-id") Long exhibitionId,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {

    if (pageNum < 1) {
      throw new CustomException(PageErrorStatus.PAGE_NOT_FOUND);
    }
    if (pageSize < 1) {
      throw new CustomException(PageErrorStatus.PAGE_SIZE_ERROR);
    }

    Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

    return ResponseEntity.status(200)
        .body(BaseResponse.success(reviewService.getReviewByExhibitionId(exhibitionId, pageable)));
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
