/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;

@Component
public class ExhibitionMapper {

  public Exhibition toExhibition(
      String imageUrl, ExhibitionRequest request, ExhibitionStatus status) {
    return Exhibition.builder()
        .thumbnailImageUrl(imageUrl)
        .title(request.getTitle())
        .description(request.getDescription())
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .address(request.getAddress())
        .accountNumber(request.getAccountNumber())
        .bankName(request.getBankName())
        .exhibitionStatus(status)
        .build();
  }

  public ExhibitionResponse toExhibitionResponse(Exhibition exhibition) {
    return ExhibitionResponse.builder()
        .exhibitionId(exhibition.getId())
        .thumbnailImageUrl(exhibition.getThumbnailImageUrl())
        .status(exhibition.getExhibitionStatus())
        .title(exhibition.getTitle())
        .startDate(exhibition.getStartDate())
        .endDate(exhibition.getEndDate())
        .address(exhibition.getAddress())
        .build();
  }
}
