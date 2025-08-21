/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.s3.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.global.s3.entity.PathName;

public interface S3Service {

  String uploadFile(PathName pathName, MultipartFile file);

  String createKeyName(PathName pathName);

  void deleteFile(String keyName);

  List<String> getAllFiles(PathName pathName);

  String extractKeyNameFromUrl(String imageUrl);

  void fileExists(String keyName);
}
