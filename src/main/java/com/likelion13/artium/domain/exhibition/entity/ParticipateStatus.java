/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ParticipateStatus {
  @Schema(description = "요청됨")
  REQUESTED,
  @Schema(description = "요청승인")
  APPROVED
}
