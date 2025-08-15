/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.controller;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionLikeResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "전시", description = "전시 관련 API")
@RequestMapping("/api/exhibitions")
public interface ExhibitionController {

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "전시 등록", description = "작품 식별자 리스트 및 전시 정보를 요청 받아 사용자의 전시를 등록합니다.")
  ResponseEntity<BaseResponse<ExhibitionDetailResponse>> createExhibition(
      @Parameter(
              description = "전시 썸네일 이미지 파일",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestPart(value = "image", required = false)
          MultipartFile image,
      @Parameter(
              description = "전시 등록 요청 정보",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @Valid
          @RequestPart(value = "request", required = false)
          ExhibitionRequest request);

  @PostMapping("/{id}/like")
  @Operation(summary = "전시 좋아요", description = "좋아요 할 전시의 식별자를 요청 받아 좋아요를 등록합니다.")
  ResponseEntity<BaseResponse<ExhibitionLikeResponse>> createExhibitionLike(
      @Parameter(description = "좋아요 할 전시 식별자", example = "1") @PathVariable Long id);

  @GetMapping("/{id}")
  @Operation(summary = "전시 단일 조회", description = "전시 정보를 반환합니다.")
  ResponseEntity<BaseResponse<ExhibitionDetailResponse>> getExhibition(@PathVariable Long id);

  @GetMapping("/draft")
  @Operation(summary = "임시저장된 전시 개수 조회", description = "사용자가 임시 저장한 전시의 총 개수를 반환합니다.")
  ResponseEntity<BaseResponse<Integer>> getExhibitionDraftCount();

  @GetMapping
  @Operation(summary = "전시 리스트 조회", description = "전시 리스트를 정렬 기준에 맞춰 페이지로 반환합니다.")
  ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByType(
      @Parameter(description = "정렬 기준", example = "HOTTEST") @RequestParam SortBy sortBy,
      @Parameter(description = "페이지 번호", example = "1") @RequestParam Integer pageNum,
      @Parameter(description = "페이지 크기", example = "3") @RequestParam Integer pageSize);

  @GetMapping("/my-page")
  @Operation(summary = "내 전시 리스트 조회", description = "사용자의 전시 리스트를 페이지로 반환합니다.")
  ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByUser(
      @Parameter(description = "등록 완료 여부", example = "true") @RequestParam Boolean fillAll,
      @Parameter(description = "페이지 번호", example = "1") @RequestParam Integer pageNum,
      @Parameter(description = "페이지 크기", example = "3") @RequestParam Integer pageSize);

  @GetMapping("/like")
  @Operation(summary = "좋아요 한 전시 리스트 조회", description = "사용자가 좋아요 한 전시 리스트를 페이지로 반환합니다.")
  ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByLike(
      @Parameter(description = "페이지 번호", example = "1") @RequestParam Integer pageNum,
      @Parameter(description = "페이지 크기", example = "3") @RequestParam Integer pageSize);

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "전시 수정", description = "작품 식별자 리스트 및 전시 정보를 요청 받아 사용자의 전시를 수정합니다.")
  ResponseEntity<BaseResponse<ExhibitionDetailResponse>> updateExhibition(
      @Parameter(description = "전시 식별자", example = "1") @PathVariable Long id,
      @Parameter(
              description = "수정할 썸네일 이미지 파일",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestPart(value = "image", required = false)
          MultipartFile image,
      @Parameter(
              description = "전시 수정 요청 정보",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @Valid
          @RequestPart(value = "request", required = false)
          ExhibitionRequest request);

  @DeleteMapping("/{id}/like")
  @Operation(summary = "전시 좋아요 취소", description = "좋아요를 취소할 전시의 식별자를 요청 받아 좋아요를 삭제합니다.")
  ResponseEntity<BaseResponse<ExhibitionLikeResponse>> deleteExhibitionLike(
      @Parameter(description = "좋아요 했던 전시 식별자", example = "1") @PathVariable Long id);
}
