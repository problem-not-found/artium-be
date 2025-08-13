/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum SortBy {
  @Schema(description = "인기순")
  HOTTEST,
  @Schema(description = "최신순")
  LATEST
}
