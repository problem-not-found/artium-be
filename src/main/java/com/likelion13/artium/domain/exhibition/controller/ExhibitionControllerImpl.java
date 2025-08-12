/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionDetailResponse;
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
      @RequestPart(required = false) MultipartFile image,
      @RequestPart(required = false) ExhibitionRequest request) {

    return ResponseEntity.ok(
        BaseResponse.success(exhibitionService.createExhibition(image, request)));
  }

  @Override
  public ResponseEntity<BaseResponse<ExhibitionDetailResponse>> getExhibition(
      @PathVariable Long id) {

    return ResponseEntity.ok(BaseResponse.success(exhibitionService.getExhibition(id)));
  }

  @Override
  public ResponseEntity<BaseResponse<Integer>> getExhibitionDraftCount() {

    return ResponseEntity.ok(BaseResponse.success(exhibitionService.getExhibitionDraftCount()));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByType(
      @RequestParam SortBy sortBy, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.ok(
        BaseResponse.success(exhibitionService.getExhibitionPageByType(sortBy, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPageByUser(
      @RequestParam Boolean fillAll,
      @RequestParam Integer pageNum,
      @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.ok(
        BaseResponse.success(exhibitionService.getExhibitionPageByUser(fillAll, pageable)));
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

  @Override
  public ResponseEntity<BaseResponse<ExhibitionDetailResponse>> updateExhibition(
      @PathVariable Long id,
      @RequestPart("image") MultipartFile image,
      @Valid @RequestPart("request") ExhibitionRequest request) {

    return ResponseEntity.ok(
        BaseResponse.success(exhibitionService.updateExhibition(id, image, request)));
  }
}
