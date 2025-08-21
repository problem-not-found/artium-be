/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "LikeResponse DTO", description = "사용자 좋아요 요청에 대한 응답 반환")
public class UserLikeResponse {

  @Schema(description = "좋아요를 보낸 사용자 코드", example = "heejun0109")
  private String currentUserCode;

  @Schema(description = "좋아요를 받은 사용자 코드", example = "simonisnextdoor")
  private String targetUserCode;
}
