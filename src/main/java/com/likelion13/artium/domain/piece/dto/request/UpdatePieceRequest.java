/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.dto.request;

import java.util.List;

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

  @Schema(description = "작품 제목", example = "삿포로의 눈 마을 축제")
  private String title;

  @Schema(description = "작품 설명", example = "매년 삿포로에서 하는 눈 마을 축제를 그린 그림.")
  private String description;

  @Schema(description = "구매 가능 여부", example = "true")
  private Boolean isPurchasable;

  @Schema(description = "남길 작품 식별자 리스트", example = "[1, 3]")
  private List<Long> remainPieceDetailIds;
}
