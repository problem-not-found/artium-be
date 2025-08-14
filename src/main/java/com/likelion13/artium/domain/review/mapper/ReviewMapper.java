/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.review.dto.request.ReviewRequest;
import com.likelion13.artium.domain.review.dto.response.ReviewResponse;
import com.likelion13.artium.domain.review.entity.Review;
import com.likelion13.artium.domain.user.entity.User;

@Component
public class ReviewMapper {

  public Review toReview(ReviewRequest request, Exhibition exhibition, User user) {
    return Review.builder().content(request.getContent()).exhibition(exhibition).user(user).build();
  }

  public ReviewResponse toReviewResponse(Review review) {
    return ReviewResponse.builder()
        .reviewId(review.getId())
        .content(review.getContent())
        .createdAt(review.getCreatedAt())
        .build();
  }
}
