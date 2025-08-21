/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ProgressStatus {
  @Schema(description = "대기 중")
  WAITING,
  @Schema(description = "등록 거절")
  UNREGISTERED,
  @Schema(description = "등록 완료")
  REGISTERED,
  @Schema(description = "전시 중")
  ON_DISPLAY,
}
