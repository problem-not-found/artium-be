/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.qdrant.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum CollectionName {
  @Schema(description = "작품 컬렉션")
  PIECE,
  @Schema(description = "전시 컬렉션")
  EXHIBITION,
  @Schema(description = "사용자 컬렉션")
  USER
}
