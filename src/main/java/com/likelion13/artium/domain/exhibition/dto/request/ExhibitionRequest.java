/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.likelion13.artium.domain.exhibition.entity.BankName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "TraditionRequest DTO", description = "전통문화 생성을 위한 데이터 전송")
public class ExhibitionRequest {

  @Schema(description = "전시에 포함될 작품 ID 리스트", example = "[1, 2, 3]")
  private List<Long> pieceIdList;

  @Schema(description = "전시 제목", example = "성북구 신인 작가 합동 전시: 두 번째 여름")
  private String title;

  @Schema(description = "전시 설명", example = "이번 전시는 성북구의 신인 작가들이 모여서 개최한 전시입니다.")
  private String description;

  @Schema(description = "시작일", example = "2025-01-01")
  private LocalDate startDate;

  @Schema(description = "종료일", example = "2025-12-31")
  private LocalDate endDate;

  @Schema(description = "오프라인 전시 주소", example = "남대문로 9길 40")
  private String address;

  @Schema(description = "오프라인 전시 설명", example = "이 전시는 남대문로에서 진행됩니다.")
  private String offlineDescription;

  @Schema(description = "거래 계좌번호", example = "93800200854555")
  private String accountNumber;

  @Schema(description = "거래 은행", example = "KOOKMIN")
  private BankName bankName;
}
