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
@Schema(title = "PreferenceResponse DTO", description = "사용자 관심사 조회에 대한 응답 반환")
public class PreferenceResponse {

  @Schema(description = "사용자 식별자", example = "1")
  private Long userId;

  @Schema(description = "연령대", example = "20대")
  private String age;

  @Schema(description = "성별", example = "남성")
  private String gender;

  @Schema(description = "주제 취향", example = "[\"전통 민속\", \"초상화\"]")
  private List<String> themePreferences;

  @Schema(description = "분위기 취향", example = "[\"휴양지 감성\", \"몽환적\"]")
  private List<String> moodPreferences;

  @Schema(description = "형식 취향", example = "[\"수채화\", \"아크릴화\"]")
  private List<String> formatPreferences;
}
