/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserParticipateResponse DTO", description = "사용자 전시 참여 정보 응답 반환")
public class UserParticipateResponse {

  @Schema(description = "전시 식별자", example = "1")
  private Long exhibitionId;

  @Schema(description = "썸네일 사진 URL", example = "")
  private String thumbnailImageUrl;

  @Schema(description = "전시 제목", example = "성북구 신인 작가 합동 전시: 두 번째 여름")
  private String title;
}
