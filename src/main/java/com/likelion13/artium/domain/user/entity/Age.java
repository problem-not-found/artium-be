/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "연령대 Enum")
@Getter
@RequiredArgsConstructor
public enum Age {
  @Schema(description = "10대")
  TEENS("10대"),
  @Schema(description = "20대")
  TWENTIES("20대"),
  @Schema(description = "30대")
  THIRTIES("30대"),
  @Schema(description = "40대")
  FORTIES("40대"),
  @Schema(description = "50대+")
  FIFTIES_PLUS("50대 이상");

  private final String ko;
}
