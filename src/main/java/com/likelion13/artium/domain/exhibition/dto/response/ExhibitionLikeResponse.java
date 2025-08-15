/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExhibitionLikeResponse DTO", description = "전시 좋아요 정보 응답 반환")
public class ExhibitionLikeResponse {

  @Schema(description = "전시 식별자", example = "1")
  private Long exhibitionId;

  @Schema(description = "좋아요를 보낸 사용자 닉네임", example = "윤희준")
  private String currentUserNickname;
}
