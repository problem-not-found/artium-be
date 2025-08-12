/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
import com.likelion13.artium.domain.user.entity.User;

@Component
public class ExhibitionMapper {

  public Exhibition toExhibition(
      String imageUrl, ExhibitionRequest request, ExhibitionStatus status, User user) {
    return Exhibition.builder()
        .thumbnailImageUrl(imageUrl)
        .title(request.getTitle())
        .description(request.getDescription())
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .address(request.getAddress())
        .offlineDescription(request.getOfflineDescription())
        .accountNumber(request.getAccountNumber())
        .bankName(request.getBankName())
        .exhibitionStatus(status)
        .fillAll(validateExhibitionFields(imageUrl, request, status))
        .user(user)
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

  public ExhibitionDetailResponse toExhibitionDetailResponse(Exhibition exhibition) {
    return ExhibitionDetailResponse.builder()
        .exhibitionId(exhibition.getId())
        .thumbnailImageUrl(exhibition.getThumbnailImageUrl())
        .status(exhibition.getExhibitionStatus())
        .title(exhibition.getTitle())
        .description(exhibition.getDescription())
        .startDate(exhibition.getStartDate())
        .endDate(exhibition.getEndDate())
        .address(exhibition.getAddress())
        .offlineDescription(exhibition.getOfflineDescription())
        .accountNumber(exhibition.getAccountNumber())
        .bankName(exhibition.getBankName())
        .exhibitionStatus(exhibition.getExhibitionStatus())
        .fillAll(exhibition.getFillAll())
        .build();
  }

  private boolean validateExhibitionFields(
      String imageUrl, ExhibitionRequest request, ExhibitionStatus status) {
    return imageUrl != null
        && request.getTitle() != null
        && request.getDescription() != null
        && request.getStartDate() != null
        && request.getEndDate() != null
        && request.getAddress() != null
        && request.getOfflineDescription() != null
        && request.getAccountNumber() != null
        && request.getBankName() != null
        && status != null;
  }
}
