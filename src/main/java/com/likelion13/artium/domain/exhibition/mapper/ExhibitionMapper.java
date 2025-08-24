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
        .addressName(request.getAddressName())
        .offlineDescription(request.getOfflineDescription())
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

  public ExhibitionResponse toExhibitionResponse(Exhibition exhibition, boolean isLike) {
    return ExhibitionResponse.builder()
        .exhibitionId(exhibition.getId())
        .isLike(isLike)
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
        .addressName(exhibition.getAddressName())
        .offlineDescription(exhibition.getOfflineDescription())
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
        && request.getAddressName() != null
        && request.getOfflineDescription() != null
        && status != null;
  }
}
