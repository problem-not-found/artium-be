/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.likelion13.artium.domain.piece.entity.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "CreatePieceRequest DTO", description = "작품 등록을 위한 데이터 DTO")
public class CreatePieceRequest {

  @NotBlank(message = "작품 제목은 필수입니다.")
  @Schema(description = "작품 제목", example = "제주도의 푸른 바다")
  private String title;

  @NotBlank(message = "작품 설명은 필수입니다.")
  @Schema(description = "작품 설명", example = "제주도 여행을 떠나 그린 푸른 바다. 푸른 빛이 맴돈다")
  private String description;

  @NotNull(message = "구매 가능 여부는 필수입니다.")
  @Schema(description = "구매 가능 여부", example = "true")
  private Boolean isPurchasable;

  @Schema(description = "작품 상태(임시저장: DRAFT, 저장: REGISTERED", example = "REGISTERED")
  private Status status;
}
