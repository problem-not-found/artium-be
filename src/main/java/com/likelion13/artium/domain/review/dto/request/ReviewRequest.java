/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ReviewRequest DTO", description = "감상평 등록을 위한 데이터 DTO")
public class ReviewRequest {

  @NotBlank(message = "감상평 내용 항목은 필수입니다.")
  @Schema(description = "감상평 내용", example = "재미있었습니다.")
  private String content;
}
