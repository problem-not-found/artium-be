/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceFeedResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.global.page.response.PageResponse;

public interface PieceService {

  /**
   * @param userId 작품 리스트의 사용자 식별자
   * @param pageable 페이징 처리 값 객체
   * @return 특정 사용자의 작품 리스트
   */
  PageResponse<PieceSummaryResponse> getPiecePage(Long userId, Pageable pageable);

  /**
   * @param applicated 등록 신청 여부
   * @param pageable 페이징 처리 값 객체
   * @return 내 작품 리스트
   */
  PageResponse<PieceSummaryResponse> getMyPiecePage(Boolean applicated, Pageable pageable);

  /**
   * @param createPieceRequest 작품 등록 요청 정보
   * @param saveStatus 작품 저장 상태
   * @param mainImage 작품 메인 이미지 (S3 업로드 로직 포함)
   * @param detailImages 작품 디테일 컷들 (S3 업로드 로직, 없는 경우 처리 로직 포함)
   * @return 작품 정보 응답
   */
  PieceSummaryResponse createPiece(
      CreatePieceRequest createPieceRequest,
      SaveStatus saveStatus,
      MultipartFile mainImage,
      List<MultipartFile> detailImages);

  /**
   * @param pieceId 작품 식별자 (크리에이터가 아닐 시 임시저장, 미승인, 등록 실패 작품 조회 불가 로직 포함)
   * @return 작품 정보 응답
   */
  PieceResponse getPiece(Long pieceId);

  /**
   * @param pieceId 작품 식별자
   * @param updatePieceRequest 작품 수정 요청 정보 (남길 디테일 컷 식별자 리스트 포함)
   * @param saveStatus 작품 저장 상태
   * @param mainImage 작품 메인 이미지 (존재 시 S3 업로드 로직 포함)
   * @param detailImages 작품 디테일 컷들 (S3 업로드 로직, 없는 경우 처리 로직 포함)
   * @return 작품 정보 응답
   */
  PieceResponse updatePiece(
      Long pieceId,
      UpdatePieceRequest updatePieceRequest,
      SaveStatus saveStatus,
      MultipartFile mainImage,
      List<MultipartFile> detailImages);

  /**
   * @param pieceIds 작품 식별자 리스트 (크리에이터가 아닐 시 삭제 실패)
   * @return 작품 삭제 성공 메시지
   */
  String deletePieces(List<Long> pieceIds);

  /**
   * @return 임시 저장된 작품의 개수 (크리에이터가 아닐 시 조회 실패)
   */
  Integer getPieceDraftCount();

  /**
   * 좋아요 한 작품 리스트 조회 메서드
   *
   * @param pageable 페이징 처리값 객체
   * @return 좋아요 한 작품 리스트
   */
  PageResponse<PieceSummaryResponse> getLikePieces(Pageable pageable);

  /**
   * 좋아요 기반 추천 작품 리스트 조회 메서드
   *
   * @param pageable 페이징 처리값 객체
   * @return 추천 작품 리스트
   */
  PageResponse<PieceSummaryResponse> getRecommendationPiecePage(Pageable pageable);

  /**
   * 피드의 작품 리스트 조회 메서드
   *
   * @param sortBy 인기순, 최신순 정렬 기준
   * @param pageable 페이징 처리값 객체
   * @return 작품 리스트
   */
  PageResponse<PieceFeedResponse> getPiecePageByType(SortBy sortBy, Pageable pageable);
}
