/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.s3.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.likelion13.artium.global.config.S3Config;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.s3.entity.PathName;
import com.likelion13.artium.global.s3.exception.S3ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  private final AmazonS3 amazonS3;
  private final S3Config s3Config;

  @Override
  public String uploadFile(PathName pathName, MultipartFile file) {

    validateFile(file);

    String keyName = createKeyName(pathName);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try {
      amazonS3.putObject(
          new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));
      log.info("파일 업로드 성공 - keyName: {}", keyName);
      return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    } catch (Exception e) {
      log.error("S3 upload 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  @Override
  public String createKeyName(PathName pathName) {

    return getPrefix(pathName) + '/' + UUID.randomUUID();
  }

  @Override
  public void deleteFile(String keyName) {

    fileExists(keyName);

    try {
      amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), keyName));
      log.info("파일 삭제 성공 - keyName: {}", keyName);
    } catch (Exception e) {
      log.error("S3 delete 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  @Override
  public List<String> getAllFiles(PathName pathName) {
    String prefix = getPrefix(pathName);
    try {
      List<String> urls =
          amazonS3
              .listObjectsV2(
                  new ListObjectsV2Request()
                      .withBucketName(s3Config.getBucket())
                      .withPrefix(prefix))
              .getObjectSummaries()
              .stream()
              .map(obj -> amazonS3.getUrl(s3Config.getBucket(), obj.getKey()).toString())
              .collect(Collectors.toList());
      log.info("파일 목록 조회 성공 - pathName: {}, 파일 수: {}", pathName, urls.size());
      return urls;
    } catch (Exception e) {
      log.error("S3 파일 목록 조회 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  @Override
  public String extractKeyNameFromUrl(String imageUrl) {

    String bucketUrl =
        "https://" + s3Config.getBucket() + ".s3." + s3Config.getRegion() + ".amazonaws.com/";
    if (!imageUrl.startsWith(bucketUrl)) {
      throw new CustomException(S3ErrorCode.FILE_URL_INVALID);
    }
    String keyName = imageUrl.substring(bucketUrl.length());
    log.info("keyName 추출 성공 - keyName: {}", keyName);
    return keyName;
  }

  public void fileExists(String keyName) {

    if (!amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
      throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
    }
  }

  private void validateFile(MultipartFile file) {

    if (file.getSize() > 5 * 1024 * 1024) {
      throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }
  }

  private String getPrefix(PathName pathName) {
    return switch (pathName) {
      case PROFILE_IMAGE -> s3Config.getProfileImagePath();
      case PIECE -> s3Config.getPiecePath();
    };
  }
}
