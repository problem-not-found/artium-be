/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "PieceFeedResponse", description = "피드에 나오는 작품의 응답 반환")
public class PieceFeedResponse {

  @Schema(description = "작품 식별자", example = "1")
  private Long pieceId;

  @Schema(description = "작품 제목", example = "제주도의 집")
  private String title;

  @Schema(description = "작품 이미지")
  private String imageUrl;

  @Schema(description = "요청 유저의 좋아요 여부")
  private Boolean isLike;
}
