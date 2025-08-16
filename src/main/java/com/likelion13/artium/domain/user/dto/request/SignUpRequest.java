/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "SignUpRequest DTO", description = "사용자 회원가입을 위한 데이터 전송")
public class SignUpRequest {

  @NotBlank(message = "사용자 아이디 항목은 필수입니다.")
  @Schema(description = "사용자 아이디", example = "heejun0109")
  private String username;

  @NotBlank(message = "비밀번호 항목은 필수입니다.")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}$",
      message = "비밀번호는 최소 8자 이상, 숫자 및 특수문자를 포함해야 합니다.")
  @Schema(description = "비밀번호", example = "password123!")
  private String password;

  @NotBlank(message = "사용자 닉네임 항목은 필수입니다.")
  @Schema(description = "사용자 닉네임", example = "아르티움")
  private String nickname;

  @NotBlank(message = "사용자 코드 항목은 필수입니다. 중복될 수 없습니다.")
  @Schema(description = "사용자 코드", example = "simonisnextdoor")
  private String code;
}
