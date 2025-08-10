/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum Status {
  @Schema(description = "임시 저장")
  DRAFT,
  @Schema(description = "미승인")
  UNREGISTERED,
  @Schema(description = "등록 실패")
  FAILED,
  @Schema(description = "등록")
  REGISTERED,
  @Schema(description = "전시 중")
  ON_DISPLAY,
}
