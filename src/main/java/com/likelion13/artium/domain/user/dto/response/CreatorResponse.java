/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "CreatorResponse DTO", description = "크리에이터 정보 응답 반환")
public class CreatorResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "닉네임", example = "김땡땡")
  private String nickname;

  @Schema(description = "코드", example = "simonisnextdoor")
  private String code;

  @Schema(
      description = "프로필 이미지 URL",
      example = "http://k.kakaocdn.net/dn/oOPCG/btsPjlOHjk6/6jx0PyBKkHHyCfbV8IY741/img_640x640.jpg")
  private String profileImageUrl;

  @Schema(description = "사용자 소개", example = "안녕하세요. 아름다운 바다를 좋아하는")
  private String introduction;

  @Schema(description = "요청 사용자의 좋아요 여부", example = "false")
  private Boolean isLike;
}
