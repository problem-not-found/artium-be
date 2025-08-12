/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
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
  public ResponseEntity<BaseResponse<String>> createExhibition(
      @RequestPart(required = false) MultipartFile image,
      @RequestPart(required = false) ExhibitionRequest request) {

    return ResponseEntity.ok(
        BaseResponse.success(exhibitionService.createExhibition(image, request)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<ExhibitionResponse>>> getExhibitionPage(
      @RequestParam Integer pageNum, @RequestParam Integer pageSize) {

    if (pageNum < 1) {
      throw new CustomException(PageErrorStatus.PAGE_NOT_FOUND);
    }
    if (pageSize < 1) {
      throw new CustomException(PageErrorStatus.PAGE_SIZE_ERROR);
    }

    Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

    return ResponseEntity.ok(BaseResponse.success(exhibitionService.getExhibitionPage(pageable)));
  }
}
