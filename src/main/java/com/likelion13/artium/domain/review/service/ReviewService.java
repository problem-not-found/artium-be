/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.service;

import org.springframework.data.domain.Pageable;

import com.likelion13.artium.domain.review.dto.request.ReviewRequest;
import com.likelion13.artium.domain.review.dto.response.ReviewResponse;
import com.likelion13.artium.global.page.response.PageResponse;

/**
 * 리뷰 관련 주요 기능을 제공하는 서비스 인터페이스입니다.
 *
 * <p>주요 기능:
 *
 * <ul>
 *   <li>리뷰 생성
 *   <li>특정 전시회 리뷰 조회
 *   <li>리뷰 수정
 *   <li>리뷰 삭제
 * </ul>
 */
public interface ReviewService {

  /**
   * 특정 전시회에 대한 리뷰를 생성합니다.
   *
   * @param exhibitionId 리뷰를 작성할 전시회 ID
   * @param request 리뷰 작성 요청 데이터 (내용, 평점 등)
   * @return 생성된 리뷰의 응답 DTO
   */
  ReviewResponse createReview(Long exhibitionId, ReviewRequest request);

  /**
   * 특정 전시회에 등록된 모든 리뷰를 조회합니다.
   *
   * @param exhibitionId 조회할 전시회 ID
   * @return 해당 전시회의 모든 리뷰 목록
   */
  PageResponse<ReviewResponse> getReviewByExhibitionId(Long exhibitionId, Pageable pageable);

  /**
   * 특정 전시회에 등록된 리뷰를 수정합니다.
   *
   * @param exhibitionId 리뷰가 속한 전시회 ID
   * @param reviewId 수정할 리뷰 ID
   * @param request 리뷰 수정 요청 데이터
   * @return 수정된 리뷰의 응답 DTO
   */
  ReviewResponse updateReview(Long exhibitionId, Long reviewId, ReviewRequest request);

  /**
   * 특정 전시회에 등록된 리뷰를 삭제합니다.
   *
   * @param exhibitionId 리뷰가 속한 전시회 ID
   * @param reviewId 삭제할 리뷰 ID
   * @return 삭제 완료 메시지 문자열
   */
  String deleteReview(Long exhibitionId, Long reviewId);
}
