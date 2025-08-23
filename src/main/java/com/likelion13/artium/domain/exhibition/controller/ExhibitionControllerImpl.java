/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionPiecesUpdateRequest;
import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionLikeResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionPiecesUpdateResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.domain.exhibition.service.ExhibitionService;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.exception.PageErrorStatus;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ExhibitionControllerImpl implements ExhibitionController {

  private final ExhibitionService exhibitionService;

  @Override
  public ResponseEntity<BaseResponse<ExhibitionDetailResponse>> createExhibition(
      @RequestPart(value = "image", required = false) MultipartFile image,
      @Valid @RequestPart(value = "request", required = false) ExhibitionRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.success(
                201, "전시 정보 생성에 성공하였습니다.", exhibitionService.createExhibition(image, request)));
  }

  @Override
  public ResponseEntity<BaseResponse<ExhibitionLikeResponse>> createExhibitionLike(
      @PathVariable Long id) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.success(
                201, "전시 좋아요에 성공하였습니다.", exhibitionService.createExhibitionLike(id)));
  }

  @Override
  public ResponseEntity<BaseResponse<ExhibitionDetailResponse>> getExhibition(
      @PathVariable Long id) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success(200, "전시 정보 조회에 성공하였습니다.", exhibitionService.getExhibition(id)));
  }

  @Override
  public ResponseEntity<BaseResponse<Integer>> getExhibitionDraftCount() {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200, "임시저장된 전시 개수 조회에 성공하였습니다.", exhibitionService.getExhibitionDraftCount()));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByType(
      @RequestParam SortBy sortBy, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200,
                sortBy.toString() + " 기준으로 전시 페이지 조회에 성공하였습니다.",
                exhibitionService.getExhibitionPageByType(sortBy, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByUser(
      @RequestParam Boolean fillAll,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200,
                "사용자가 등록한 전시 페이지 조회에 성공하였습니다.",
                exhibitionService.getExhibitionPageByUser(fillAll, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByUserId(
      @RequestParam Long userId, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200,
                "전시 페이지 조회에 성공하였습니다.",
                exhibitionService.getExhibitionPageByUserId(userId, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByLike(
      @RequestParam Integer pageNum, @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200,
                "사용자가 좋아요 한 전시 페이지 조회에 성공하였습니다.",
                exhibitionService.getExhibitionPageByLike(pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>>
      getRecommendationExhibitionPage(
          @RequestParam Boolean opposite,
          @RequestParam Integer pageNum,
          @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200,
                "전시 추천 페이지 조회에 성공하였습니다.",
                exhibitionService.getRecommendationExhibitionPage(opposite, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<List<ExhibitionResponse>>> getExhibitionListByKeyword(
      @RequestParam String keyword, @RequestParam SortBy sortBy) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200,
                "전시 검색 리스트 조회에 성공하였습니다.",
                exhibitionService.getExhibitionListByKeyword(keyword, sortBy)));
  }

  @Override
  public ResponseEntity<BaseResponse<ExhibitionDetailResponse>> updateExhibition(
      @PathVariable Long id,
      @RequestPart(value = "image", required = false) MultipartFile image,
      @Valid @RequestPart(value = "request", required = false) ExhibitionRequest request) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200, "전시 정보 수정에 성공하였습니다.", exhibitionService.updateExhibition(id, image, request)));
  }

  @Override
  public ResponseEntity<BaseResponse<ExhibitionPiecesUpdateResponse>> updateExhibitionPieces(
      @PathVariable Long id, @RequestBody ExhibitionPiecesUpdateRequest request) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200, "작품 리스트 수정에 성공하였습니다.", exhibitionService.updateExhibitionPieces(id, request)));
  }

  @Override
  public ResponseEntity<BaseResponse<ExhibitionLikeResponse>> deleteExhibitionLike(
      @PathVariable Long id) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.success(
                200, "전시 좋아요 삭제에 성공하였습니다.", exhibitionService.deleteExhibitionLike(id)));
  }

  private Pageable validatePageable(Integer pageNum, Integer pageSize) {
    if (pageNum < 1) {
      throw new CustomException(PageErrorStatus.PAGE_NOT_FOUND);
    }
    if (pageSize < 1) {
      throw new CustomException(PageErrorStatus.PAGE_SIZE_ERROR);
    }

    return PageRequest.of(pageNum - 1, pageSize);
  }
}
