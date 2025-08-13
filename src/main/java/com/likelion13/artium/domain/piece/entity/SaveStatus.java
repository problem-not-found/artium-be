/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum SaveStatus {
  @Schema(description = "임시 저장")
  DRAFT,
  @Schema(description = "등록 신청 중")
  APPLICATION,
}
