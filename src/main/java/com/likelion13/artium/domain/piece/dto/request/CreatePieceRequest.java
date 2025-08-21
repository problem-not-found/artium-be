/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "CreatePieceRequest DTO", description = "작품 등록을 위한 데이터 DTO")
public class CreatePieceRequest {

  @Schema(description = "작품 제목", example = "제주도의 푸른 바다")
  private String title;

  @Schema(description = "작품 설명", example = "제주도 여행을 떠나 그린 푸른 바다. 푸른 빛이 맴돈다")
  private String description;

  @Schema(description = "구매 가능 여부", example = "true")
  private Boolean isPurchasable;
}
