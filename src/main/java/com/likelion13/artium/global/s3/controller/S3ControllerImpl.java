/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.s3.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.global.response.BaseResponse;
import com.likelion13.artium.global.s3.entity.PathName;
import com.likelion13.artium.global.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequiredArgsConstructor
public class S3ControllerImpl implements S3Controller {

  private final S3Service s3Service;
  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public ResponseEntity<BaseResponse<String>> uploadFile(
      @RequestParam PathName pathName, MultipartFile file) {

    String imageUrl = s3Service.uploadFile(pathName, file);
    return ResponseEntity.ok(BaseResponse.success(imageUrl));
  }

  @Override
  public ResponseEntity<BaseResponse<List<String>>> getFileList(@RequestParam PathName pathName) {
    List<String> files = s3Service.getAllFiles(pathName);
    return ResponseEntity.ok(BaseResponse.success(files));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deleteFile(@PathVariable String keyName) {
    s3Service.deleteFile(keyName);
    return ResponseEntity.ok(BaseResponse.success("파일이 성공적으로 삭제되었습니다."));
  }

  @Override
  public ResponseEntity<ByteArrayResource> getPiece(@PathVariable String filename) {

    String s3Url = "https://likelion13-artium.s3.ap-northeast-2.amazonaws.com/piece/" + filename;

    byte[] fileBytes = restTemplate.getForObject(s3Url, byte[].class);
    ByteArrayResource resource = new ByteArrayResource(fileBytes);

    log.info("이미지 프록시 요청 성공: " + s3Url);

    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
  }
}
