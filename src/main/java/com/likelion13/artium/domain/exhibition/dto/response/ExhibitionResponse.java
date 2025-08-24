/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.dto.response;

import java.time.LocalDate;

import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExhibitionResponse DTO", description = "전시 정보 응답 반환")
public class ExhibitionResponse {

  @Schema(description = "전시 식별자", example = "1")
  private Long exhibitionId;

  @Schema(description = "전시 좋아요 여부", example = "true")
  private Boolean isLike;

  @Schema(description = "썸네일 사진 URL", example = "")
  private String thumbnailImageUrl;

  @Schema(description = "전시 상태", example = "UPCOMING")
  private ExhibitionStatus status;

  @Schema(description = "전시 제목", example = "성북구 신인 작가 합동 전시: 두 번째 여름")
  private String title;

  @Schema(description = "시작일", example = "2025-01-01")
  private LocalDate startDate;

  @Schema(description = "종료일", example = "2025-12-31")
  private LocalDate endDate;

  @Schema(description = "오프라인 전시 주소", example = "남대문로 9길 40")
  private String address;
}
