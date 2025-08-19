/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionLikeResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionSummaryResponse;
import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionLike;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionPiece;
import com.likelion13.artium.domain.user.entity.User;

@Component
public class ExhibitionMapper {

  public Exhibition toExhibition(
      String imageUrl,
      ExhibitionRequest request,
      ExhibitionStatus status,
      User user,
      List<ExhibitionPiece> exhibitionPieceList,
      List<ExhibitionParticipant> exhibitionParticipantList) {
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
        .exhibitionPieces(exhibitionPieceList)
        .exhibitionParticipants(exhibitionParticipantList)
        .build();
  }

  public ExhibitionLike toExhibitionLike(Exhibition exhibition, User user) {
    return ExhibitionLike.builder().exhibition(exhibition).user(user).build();
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

  public ExhibitionSummaryResponse toExhibitionSummaryResponse(Exhibition exhibition) {
    return ExhibitionSummaryResponse.builder()
        .exhibitionId(exhibition.getId())
        .thumbnailImageUrl(exhibition.getThumbnailImageUrl())
        .title(exhibition.getTitle())
        .build();
  }

  public ExhibitionDetailResponse toExhibitionDetailResponse(
      Exhibition exhibition,
      Boolean isAuthor,
      Boolean isLike,
      List<Long> pieceIdList,
      List<Long> participantIdList) {
    return ExhibitionDetailResponse.builder()
        .exhibitionId(exhibition.getId())
        .isAuthor(isAuthor)
        .thumbnailImageUrl(exhibition.getThumbnailImageUrl())
        .pieceIdList(pieceIdList)
        .status(exhibition.getExhibitionStatus())
        .isLike(isLike)
        .title(exhibition.getTitle())
        .userId(exhibition.getUser().getId())
        .description(exhibition.getDescription())
        .startDate(exhibition.getStartDate())
        .endDate(exhibition.getEndDate())
        .participantIdList(participantIdList)
        .address(exhibition.getAddress())
        .offlineDescription(exhibition.getOfflineDescription())
        .accountNumber(exhibition.getAccountNumber())
        .bankName(exhibition.getBankName())
        .fillAll(exhibition.getFillAll())
        .build();
  }

  public ExhibitionLikeResponse toExhibitionLikeResponse(ExhibitionLike exhibitionLike) {
    return ExhibitionLikeResponse.builder()
        .exhibitionId(exhibitionLike.getExhibition().getId())
        .currentUserNickname(exhibitionLike.getUser().getNickname())
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
