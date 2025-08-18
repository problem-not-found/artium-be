/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.page.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceFeedResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.review.dto.response.ReviewResponse;
import com.likelion13.artium.domain.user.dto.response.CreatorFeedResponse;
import com.likelion13.artium.domain.user.dto.response.UserSummaryResponse;
import com.likelion13.artium.global.page.response.PageResponse;

@Component
public class PageMapper {

  private <T> PageResponse<T> toPageResponse(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageNum(page.getNumber())
        .pageSize(page.getSize())
        .last(page.isLast())
        .first(page.isFirst())
        .build();
  }

  public PageResponse<ExhibitionResponse> toExhibitionPageResponse(Page<ExhibitionResponse> page) {
    return toPageResponse(page);
  }

  public PageResponse<PieceSummaryResponse> toPiecePageResponse(Page<PieceSummaryResponse> page) {
    return toPageResponse(page);
  }

  public PageResponse<PieceFeedResponse> toPieceFeedPageResponse(Page<PieceFeedResponse> page) {
    return toPageResponse(page);
  }

  public PageResponse<ReviewResponse> toReviewPageResponse(Page<ReviewResponse> page) {
    return toPageResponse(page);
  }

  public PageResponse<UserSummaryResponse> toUserSummaryPageResponse(
      Page<UserSummaryResponse> page) {
    return toPageResponse(page);
  }

  public PageResponse<CreatorFeedResponse> toCreatorFeedPageResponse(
      Page<CreatorFeedResponse> page) {
    return toPageResponse(page);
  }
}
