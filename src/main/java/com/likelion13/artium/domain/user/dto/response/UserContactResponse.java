/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UserContactResponse", description = "사용자 연락 수단 응답 반환")
public class UserContactResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "사용자 이메일", example = "example@gmail.com")
  private String email;

  @Schema(description = "사용자 인스타그램", example = "simonisnextdoor")
  private String instagram;
}
