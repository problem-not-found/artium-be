/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.controller;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
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
  ResponseEntity<BaseResponse<String>> createExhibition(
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

  @GetMapping
  @Operation(summary = "내 전시 리스트 조회", description = "사용자의 전시 리스트를 페이지로 반환합니다.")
  ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPage(
      @Parameter(description = "페이지 번호", example = "1") @RequestParam Integer pageNum,
      @Parameter(description = "페이지 크기", example = "3") @RequestParam Integer pageSize);
}
