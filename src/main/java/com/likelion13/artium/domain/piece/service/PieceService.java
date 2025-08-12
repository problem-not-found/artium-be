/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.global.page.response.PageResponse;

public interface PieceService {

  /**
   * @param userId 작품 리스트의 사용자 식별자
   * @param pageable 페이징 처리 값 객체
   * @return
   */
  PageResponse<PieceSummaryResponse> getPiecePage(Long userId, Pageable pageable);

  /**
   * @param createPieceRequest 작품 등록 요청 정보
   * @param mainImage 작품 메인 이미지 (S3 업로드 로직 포함)
   * @param detailImages 작품 디테일 컷들 (S3 업로드 로직, 없는 경우 처리 로직 포함)
   * @return 작품 정보 응답
   */
  PieceSummaryResponse createPiece(
      CreatePieceRequest createPieceRequest,
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
   * @param mainImage 작품 메인 이미지 (존재 시 S3 업로드 로직 포함)
   * @param detailImages 작품 디테일 컷들 (S3 업로드 로직, 없는 경우 처리 로직 포함)
   * @return 작품 정보 응답
   */
  PieceResponse updatePiece(
      Long pieceId,
      UpdatePieceRequest updatePieceRequest,
      MultipartFile mainImage,
      List<MultipartFile> detailImages);

  /**
   * @param pieceId 작품 식별자 (크리에이터가 아닐 시 삭제 실패)
   */
  void deletePiece(Long pieceId);
}
