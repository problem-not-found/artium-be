/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.dto.response;

import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.piece.entity.SaveStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PieceSummaryResponse DTO", description = "작품에 대한 응답 반환(디테일 컷 리스트 미포함)")
public class PieceSummaryResponse {

  @Schema(description = "작품 식별자", example = "1")
  private Long pieceId;

  @Schema(description = "작품 제목", example = "제주도의 집")
  private String title;

  @Schema(description = "작품 설명", example = "이 작품은 제가 어렸을 적에..")
  private String description;

  @Schema(description = "작품 이미지")
  private String imageUrl;

  @Schema(description = "작품 구매 가능 여부", example = "true")
  private Boolean isPurchasable;

  @Schema(description = "작품 저장 상태", example = "APPLICATION")
  private SaveStatus saveStatus;

  @Schema(description = "작품 진행 상태", example = "WAITING")
  private ProgressStatus progressStatus;

  @Schema(description = "사용자 ID", example = "2")
  private Long userId;
}
