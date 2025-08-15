/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserSummaryResponse DTO", description = "사용자 기본 정보 응답 반환")
public class UserSummaryResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "닉네임", example = "김땡땡")
  private String nickname;

  @Schema(
      description = "프로필 이미지 URL",
      example = "http://k.kakaocdn.net/dn/oOPCG/btsPjlOHjk6/6jx0PyBKkHHyCfbV8IY741/img_640x640.jpg")
  private String profileImageUrl;
}
