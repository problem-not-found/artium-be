/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceDetail.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PieceDetailSummaryResponse DTO", description = "작품의 디테일컷에 대한 응답 (작품 아이디 제외) 반환")
public class PieceDetailSummaryResponse {

  @Schema(description = "디테일 컷 아이디", example = "1")
  private Long pieceDetailId;

  @Schema(description = "디테일컷 이미지")
  private String imageUrl;
}
