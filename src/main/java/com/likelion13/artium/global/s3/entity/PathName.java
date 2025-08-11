/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.s3.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum PathName {
  @Schema(description = "프로필사진")
  PROFILE_IMAGE,
  @Schema(description = "작품사진")
  PIECE,
  @Schema(description = "작품 디테일컷")
  PIECE_DETAIL
}
