/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "UpdateContactRequest DTO", description = "사용자 연락 정보 수정을 위한 데이터 전송")
public class UpdateContactRequest {

  @Schema(description = "사용자 이메일", example = "example@gmail.com")
  private String email;

  @Schema(description = "사용자 인스타그램", example = "simon123")
  private String instagram;
}
