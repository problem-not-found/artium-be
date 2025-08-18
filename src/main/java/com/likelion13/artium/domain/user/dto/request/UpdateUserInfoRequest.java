/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "UpdateUserInfoRequest DTO", description = "사용자 간단 정보 수정을 위한 데이터 전송")
public class UpdateUserInfoRequest {

  @NotBlank(message = "사용자 닉네임 항목은 필수입니다.")
  @Schema(description = "사용자 닉네임", example = "아르티움")
  private String nickname;

  @NotBlank(message = "사용자 코드 항목은 필수입니다. 중복될 수 없습니다.")
  @Schema(description = "사용자 코드", example = "simonisnextdoor")
  private String code;
}
