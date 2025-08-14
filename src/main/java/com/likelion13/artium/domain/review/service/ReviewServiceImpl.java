/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.likelion13.artium.global.page.mapper.PageMapper;
import com.likelion13.artium.global.page.response.PageResponse;

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
  private final PageMapper pageMapper;

  @Override
  @Transactional
  public ReviewResponse createReview(Long exhibitionId, ReviewRequest request) {

    User currentUser = userService.getCurrentUser();

    Exhibition exhibition = findExhibitionOrThrow(exhibitionId);

    Review review = reviewMapper.toReview(request, exhibition, currentUser);

    reviewRepository.save(review);

    return reviewMapper.toReviewResponse(review, true);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ReviewResponse> getReviewByExhibitionId(
      Long exhibitionId, Pageable pageable) {

    findExhibitionOrThrow(exhibitionId);

    Page<Review> reviews =
        reviewRepository.findByExhibitionIdOrderByCreatedAtDesc(exhibitionId, pageable);
    User currentUser = userService.getCurrentUser();

    Page<ReviewResponse> page =
        reviews.map(
            review -> reviewMapper.toReviewResponse(review, currentUser.equals(review.getUser())));

    return pageMapper.toReviewPageResponse(page);
  }

  @Override
  @Transactional
  public ReviewResponse updateReview(Long exhibitionId, Long reviewId, ReviewRequest request) {

    User currentUser = userService.getCurrentUser();

    findExhibitionOrThrow(exhibitionId);

    Review review = findReviewForUserOrThrow(exhibitionId, reviewId, currentUser);

    review.update(request.getContent());

    return reviewMapper.toReviewResponse(review, true);
  }

  @Override
  @Transactional
  public String deleteReview(Long exhibitionId, Long reviewId) {

    User currentUser = userService.getCurrentUser();

    Review review = findReviewForUserOrThrow(exhibitionId, reviewId, currentUser);

    reviewRepository.delete(review);

    return "리뷰가 성공적으로 삭제되었습니다.";
  }

  private Exhibition findExhibitionOrThrow(Long exhibitionId) {
    return exhibitionRepository
        .findById(exhibitionId)
        .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
  }

  private Review findReviewForUserOrThrow(Long exhibitionId, Long reviewId, User currentUser) {
    Review review =
        reviewRepository
            .findById(reviewId)
            .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getExhibition().getId().equals(exhibitionId)) {
      throw new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND);
    }

    if (!review.getUser().getId().equals(currentUser.getId())) {
      throw new CustomException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
    }

    return review;
  }
}
