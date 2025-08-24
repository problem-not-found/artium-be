/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ExhibitionParticipantsUpdateRequest DTO", description = "전시 참여자 리스트 수정을 위한 데이터 전송")
public class ExhibitionParticipantsUpdateRequest {

  @Schema(description = "전시에 포함될 작품 식별자 리스트", example = "[1, 2, 3]")
  private List<Long> participantIdList;
}
