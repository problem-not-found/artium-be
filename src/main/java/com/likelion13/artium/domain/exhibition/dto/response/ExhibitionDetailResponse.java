/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExhibitionDetailResponse DTO", description = "전시 정보 응답 반환")
public class ExhibitionDetailResponse {

  @Schema(description = "전시 식별자", example = "1")
  private Long exhibitionId;

  @Schema(description = "본인 전시 여부", example = "true")
  private Boolean isAuthor;

  @Schema(description = "썸네일 사진 URL")
  private String thumbnailImageUrl;

  @Schema(description = "작품 식별자 리스트")
  private List<Long> pieceIdList;

  @Schema(description = "전시 상태", example = "UPCOMING")
  private ExhibitionStatus status;

  @Schema(description = "전시 좋아요 여부", example = "true")
  private Boolean isLike;

  @Schema(description = "전시 제목", example = "성북구 신인 작가 합동 전시: 두 번째 여름")
  private String title;

  @Schema(description = "전시를 등록한 사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "전시 설명", example = "이번 전시는 성북구의 신인 작가들이 모여서 개최한 전시입니다.")
  private String description;

  @Schema(description = "시작일", example = "2025-01-01")
  private LocalDate startDate;

  @Schema(description = "종료일", example = "2025-12-31")
  private LocalDate endDate;

  @Schema(description = "참여자 식별자 리스트")
  private List<Long> participantIdList;

  @Schema(description = "오프라인 전시 주소", example = "서울특별시 종로구 창의문로11길 4-1")
  private String address;

  @Schema(description = "오프라인 전시 주소 이름", example = "석파정 서울 미술관 2관")
  private String addressName;

  @Schema(description = "오프라인 전시 설명", example = "이 전시는 남대문로에서 진행됩니다.")
  private String offlineDescription;

  @Schema(description = "등록 완료 여부", example = "true")
  private Boolean fillAll;
}
