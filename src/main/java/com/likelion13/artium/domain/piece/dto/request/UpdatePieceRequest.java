/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.dto.request;

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
@Schema(title = "UpdatePieceRequest DTO", description = "작품 수정을 위한 데이터 DTO")
public class UpdatePieceRequest {

  @NotBlank(message = "작품 제목은 필수입니다.")
  @Schema(description = "작품 제목", example = "삿포로의 눈 마을 축제")
  private String title;

  @NotBlank(message = "작품 설명은 필수입니다.")
  @Schema(description = "작품 설명", example = "매년 삿포로에서 하는 눈 마을 축제를 그린 그림.")
  private String description;

  @NotBlank(message = "구매 가능 여부는 필수입니다.")
  @Schema(description = "구매 가능 여부", example = "true")
  private Boolean isPurchasable;
}
