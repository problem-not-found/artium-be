/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "성별 Enum")
@Getter
@RequiredArgsConstructor
public enum Gender {
  @Schema(description = "남성")
  MALE("남성"),
  @Schema(description = "여성")
  FEMALE("여성"),
  @Schema(description = "선택 안 함")
  NOT_SPECIFIED("선택 안 함");

  private final String ko;
}
