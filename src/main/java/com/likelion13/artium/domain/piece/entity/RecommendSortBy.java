/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum RecommendSortBy {
  @Schema(description = "내 취향 저격 리스트")
  FAVORITE,
  @Schema(description = "색다른 도전 리스트")
  NEW_STYLE
}
