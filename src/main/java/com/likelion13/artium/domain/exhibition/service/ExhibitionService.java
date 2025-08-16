/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionLikeResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.global.page.response.PageResponse;

/**
 * ExhibitionService 인터페이스는 전시회(Exhibition) 관련 주요 기능들을 정의합니다.
 *
 * <p>주요 기능:
 *
 * <ul>
 *   <li>전시회 생성
 *   <li>전시회 상세 조회
 *   <li>전시회 임시 저장 개수 조회
 *   <li>전시회 목록 페이징 조회(정렬 기준에 따라)
 *   <li>사용자별 전시회 목록 페이징 조회
 *   <li>전시회 정보 수정
 * </ul>
 */
public interface ExhibitionService {

  /**
   * 전시회를 생성합니다.
   *
   * @param image 전시회 이미지 파일
   * @param request 전시회 생성에 필요한 요청 데이터
   * @return 생성된 전시회의 상세 응답 정보
   */
  ExhibitionDetailResponse createExhibition(MultipartFile image, ExhibitionRequest request);

  /**
   * 사용자가 특정 전시회를 좋아요(Like)합니다.
   *
   * @param id 좋아요를 생성할 전시회 ID
   * @return 생성된 좋아요 정보를 담은 {@link ExhibitionLikeResponse}
   */
  ExhibitionLikeResponse createExhibitionLike(Long id);

  /**
   * 전시회 ID로 전시회 상세 정보를 조회합니다.
   *
   * @param id 조회할 전시회 ID
   * @return 해당 전시회의 상세 응답 정보
   */
  ExhibitionDetailResponse getExhibition(Long id);

  /**
   * 임시 저장된 전시회(임시 저장 상태)의 개수를 조회합니다.
   *
   * @return 임시 저장된 전시회의 개수
   */
  Integer getExhibitionDraftCount();

  /**
   * 정렬 기준에 따라 전시회 목록을 페이지 단위로 조회합니다.
   *
   * @param sortBy 정렬 기준 (예: 최신순, 인기순 등)
   * @param pageable 페이징 및 정렬 정보
   * @return 전시회 목록의 페이지 응답
   */
  PageResponse<ExhibitionResponse> getExhibitionPageByType(SortBy sortBy, Pageable pageable);

  /**
   * 사용자 기준으로 전시회 목록을 페이지 단위로 조회합니다.
   *
   * @param fillAll true면 모든 전시회 조회, false면 일부 필터링 적용
   * @param pageable 페이징 및 정렬 정보
   * @return 사용자 기준 전시회 목록의 페이지 응답
   */
  PageResponse<ExhibitionResponse> getExhibitionPageByUser(Boolean fillAll, Pageable pageable);

  /**
   * 특정 사용자가 생성한 전시회 목록을 페이지 단위로 조회합니다.
   *
   * @param userId 전시회를 조회할 사용자 ID
   * @param pageable 페이징 및 정렬 정보
   * @return 해당 사용자가 생성한 전시회 목록의 페이지 응답
   */
  PageResponse<ExhibitionResponse> getExhibitionPageByUserId(Long userId, Pageable pageable);

  /**
   * 좋아요 기준으로 전시회 목록을 페이지 단위로 조회합니다.
   *
   * @param pageable 페이징 및 정렬 정보
   * @return 좋아요 수 기준으로 정렬된 전시회 목록의 페이지 응답
   */
  PageResponse<ExhibitionResponse> getExhibitionPageByLike(Pageable pageable);

  /**
   * 전시회 ID에 해당하는 전시회 정보를 수정합니다.
   *
   * @param id 수정할 전시회 ID
   * @param image 수정할 전시회 이미지 파일
   * @param request 수정할 전시회 요청 데이터
   * @return 수정된 전시회의 상세 응답 정보
   */
  ExhibitionDetailResponse updateExhibition(
      Long id, MultipartFile image, ExhibitionRequest request);

  /**
   * 사용자가 특정 전시회 좋아요(Like)를 취소합니다.
   *
   * @param id 좋아요를 삭제할 전시회 ID
   * @return 삭제된 좋아요 정보를 담은 {@link ExhibitionLikeResponse}
   */
  ExhibitionLikeResponse deleteExhibitionLike(Long id);
}
