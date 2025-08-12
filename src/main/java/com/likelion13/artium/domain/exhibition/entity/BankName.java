/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum BankName {
  @Schema(description = "국민은행")
  KOOKMIN,
  @Schema(description = "우리은행")
  WOORI,
  @Schema(description = "신한은행")
  SHINHAN,
  @Schema(description = "카카오뱅크")
  KAKAO
}
