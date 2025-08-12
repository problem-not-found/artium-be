/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.global.page.response.PageResponse;

public interface ExhibitionService {

  String createExhibition(MultipartFile image, ExhibitionRequest request);

  PageResponse<ExhibitionResponse> getExhibitionPage(Pageable pageable);
}
