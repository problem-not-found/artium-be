/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ReviewResponse DTO", description = "감상평 정보 응답 반환")
public class ReviewResponse {

  @Schema(description = "감상평 식별자", example = "1")
  private Long reviewId;

  @Schema(description = "감상평 내용", example = "재미있었습니다.")
  private String content;

  @Schema(description = "본인 작성 여부", example = "false")
  private Boolean isAuthor;
}
