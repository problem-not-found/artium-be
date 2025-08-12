/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ExhibitionStatus {
  @Schema(description = "전시예정")
  UPCOMING,
  @Schema(description = "전시중")
  ONGOING,
  @Schema(description = "전시종료")
  ENDED
}
