/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.exception.ExhibitionErrorCode;
import com.likelion13.artium.domain.exhibition.repository.ExhibitionRepository;
import com.likelion13.artium.domain.review.dto.request.ReviewRequest;
import com.likelion13.artium.domain.review.dto.response.ReviewResponse;
import com.likelion13.artium.domain.review.entity.Review;
import com.likelion13.artium.domain.review.exception.ReviewErrorCode;
import com.likelion13.artium.domain.review.mapper.ReviewMapper;
import com.likelion13.artium.domain.review.repository.ReviewRepository;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.service.UserService;
import com.likelion13.artium.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final UserService userService;
  private final ReviewRepository reviewRepository;
  private final ExhibitionRepository exhibitionRepository;
  private final ReviewMapper reviewMapper;

  @Override
  @Transactional
  public ReviewResponse createReview(Long exhibitionId, ReviewRequest request) {

    User currentUser = userService.getCurrentUser();

    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    Review review = reviewMapper.toReview(request, exhibition, currentUser);

    reviewRepository.save(review);

    return reviewMapper.toReviewResponse(review);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReviewResponse> getReviewByExhibitionId(Long exhibitionId) {

    exhibitionRepository
        .findById(exhibitionId)
        .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    List<Review> reviews = reviewRepository.findByExhibitionId(exhibitionId);

    return reviews.stream().map(reviewMapper::toReviewResponse).toList();
  }

  @Override
  @Transactional
  public ReviewResponse updateReview(Long exhibitionId, Long reviewId, ReviewRequest request) {

    User currentUser = userService.getCurrentUser();

    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    Review review =
        reviewRepository
            .findById(reviewId)
            .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getId().equals(currentUser.getId())) {
      throw new CustomException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    review.update(request.getContent());

    return reviewMapper.toReviewResponse(review);
  }

  @Override
  @Transactional
  public String deleteReview(Long exhibitionId, Long reviewId) {

    User currentUser = userService.getCurrentUser();

    Review review =
        reviewRepository
            .findById(reviewId)
            .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getId().equals(currentUser.getId())) {
      throw new CustomException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    reviewRepository.delete(review);

    return "리뷰가 성공적으로 삭제되었습니다.";
  }
}
