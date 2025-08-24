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
@Schema(title = "CreatorFeedResponse DTO", description = "사용자 피드 응답 반환")
public class CreatorFeedResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "닉네임", example = "김땡땡")
  private String nickname;

  @Schema(
      description = "프로필 이미지 URL",
      example = "http://k.kakaocdn.net/dn/oOPCG/btsPjlOHjk6/6jx0PyBKkHHyCfbV8IY741/img_640x640.jpg")
  private String profileImageUrl;

  @Schema(description = "코드", example = "simonisnextdoor")
  private String code;

  @Schema(
      description = "작품 사진 URL 리스트",
      example =
          "[\"http://k.kakaocdn.net/dn/oOPCG/btsPjlOHjk6/6jx0PyBKkHHyCfbV8IY741/img_640x640.jpg\", \"http://k.kakaocdn.net/dn/oOPCG/btsPjlOHjk6/6jx0PyBKkHHyCfbV8IY741/img_640x640.jpg\"]")
  private List<String> pieceImageUrls;

  @Schema(description = "요청 사용자의 좋아요 여부", example = "false")
  private Boolean isLike;
}
