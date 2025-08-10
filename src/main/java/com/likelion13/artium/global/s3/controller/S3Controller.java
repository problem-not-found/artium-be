/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.s3.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.global.response.BaseResponse;
import com.likelion13.artium.global.s3.entity.PathName;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "S3", description = "파일 관리 API")
@RequestMapping("/api/s3")
public interface S3Controller {

  @Operation(summary = "파일 업로드 API", description = "파일을 업로드하고 URL을 리턴받는 API")
  @PostMapping(value = "/image-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<String>> uploadFile(
      @Parameter(description = "파일 경로", example = "PROFILE_IMAGE") @RequestParam PathName pathName,
      @Parameter(description = "업로드할 파일") MultipartFile file);

  @Operation(summary = "S3 파일 전체 조회 API", description = "해당 경로의 모든 파일 목록을 조회합니다.")
  @GetMapping("/image-list")
  ResponseEntity<BaseResponse<List<String>>> getFileList(
      @Parameter(description = "파일 경로", example = "PROFILE_IMAGE") @RequestParam PathName pathName);

  @Operation(summary = "S3 파일 삭제 API", description = "파일명을 기반으로 이미지를 삭제합니다.")
  @DeleteMapping
  ResponseEntity<BaseResponse<String>> deleteFile(
      @Parameter(description = "파일 이름", example = "profile-image/43835b6e-8991-4eab-8384-5cbf7e854abb") @RequestParam
          String keyName);
}
