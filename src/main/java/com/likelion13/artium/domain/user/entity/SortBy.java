/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum SortBy {
  @Schema(description = "지금 뜨는")
  HOTTEST,
  @Schema(description = "최근 전시 오픈")
  LATEST_OPEN,
  @Schema(description = "나와 비슷한 연령대")
  PEER_GROUP
}
