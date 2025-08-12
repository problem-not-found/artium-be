/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PieceLikeResponse DTO", description = "작품 좋아요 관련 응답 반환 DTO")
public class PieceLikeResponse {

  @Schema(description = "작품 식별자", example = "1")
  private Long pieceId;

  @Schema(description = "좋아요 여부", example = "true")
  private Boolean isLiked;
}
