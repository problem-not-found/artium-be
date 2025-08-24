/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExhibitionParticipantsUpdateResponse DTO", description = "전시 참여자 리스트 수정 응답 반환")
public class ExhibitionParticipantsUpdateResponse {

  @Schema(description = "전시 식별자", example = "1")
  private Long exhibitionId;

  @Schema(description = "참여자 식별자 리스트", example = "[1, 2, 3]")
  private List<Long> participantIdList;
}
