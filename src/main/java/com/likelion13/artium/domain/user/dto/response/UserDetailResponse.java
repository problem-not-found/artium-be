/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserDetailResponse DTO", description = "사용자 정보 응답 반환")
public class UserDetailResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "아이디(이메일)", example = "example@example.com")
  private String username;

  @Schema(description = "닉네임", example = "나나나난")
  private String nickname;

  @Schema(description = "코드", example = "simonisnextdoor")
  private String code;

  @Schema(
      description = "프로필 이미지 URL",
      example = "http://k.kakaocdn.net/dn/oOPCG/btsPjlOHjk6/6jx0PyBKkHHyCfbV8IY741/img_640x640.jpg")
  private String profileImageUrl;

  @Schema(description = "참여한 전시 식별자 리스트", example = "[1, 2, 3]")
  private List<Long> exhibitionParticipantIds;

  @Schema(description = "내가 좋아요 한 사용자 식별자 리스트", example = "[1, 2, 3]")
  private List<Long> likedUsersIds;

  @Schema(description = "나를 좋아요 한 사용자 식별자 리스트", example = "[1, 2, 3]")
  private List<Long> likedByUsersIds;

  @Schema(description = "좋아요 한 전시 식별자 리스트", example = "[1, 2, 3]")
  private List<Long> exhibitionLikeIds;
}
