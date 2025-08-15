/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "작품 분위기 취향 Enum")
@Getter
@RequiredArgsConstructor
public enum MoodPreference {
  @Schema(description = "휴양지 감성")
  RESORT_VIBES("휴양지 감성"),
  @Schema(description = "몽환적")
  DREAMY("몽환적"),
  @Schema(description = "차분한")
  CALM("차분한"),
  @Schema(description = "화려한")
  GORGEOUS("화려한"),
  @Schema(description = "따뜻한")
  WARM("따뜻한"),
  @Schema(description = "시원한")
  COOL("시원한"),
  @Schema(description = "고독한")
  LONELY("고독한"),
  @Schema(description = "서정적")
  LYRICAL("서정적"),
  @Schema(description = "유머러스")
  HUMOROUS("유머러스"),
  @Schema(description = "위트 있는")
  WITTY("위트 있는"),
  @Schema(description = "철학적")
  PHILOSOPHICAL("철학적"),
  @Schema(description = "낭만적")
  ROMANTIC("낭만적"),
  @Schema(description = "에너지 넘치는")
  ENERGETIC("에너지 넘치는"),
  @Schema(description = "불안/긴장감 있는")
  ANXIOUS_OR_TENSE("불안/긴장감 있는"),
  @Schema(description = "경건한")
  REVERENT("경건한"),

  @Schema(description = "미니멀리즘")
  MINIMALISM("미니멀리즘"),
  @Schema(description = "팝아트")
  POP_ART("팝아트"),
  @Schema(description = "빈티지")
  VINTAGE("빈티지"),
  @Schema(description = "레트로")
  RETRO("레트로"),
  @Schema(description = "퓨처리즘")
  FUTURISM("퓨처리즘"),
  @Schema(description = "아방가르드")
  AVANT_GARDE("아방가르드"),
  @Schema(description = "네온 감성")
  NEON_VIBES("네온 감성"),
  @Schema(description = "자연주의")
  NATURALISM("자연주의"),
  @Schema(description = "키치")
  KITSCH("키치"),
  @Schema(description = "그로테스크")
  GROTESQUE("그로테스크"),
  @Schema(description = "초현실주의")
  SURREALISM("초현실주의"),
  @Schema(description = "사실주의")
  REALISM("사실주의"),
  @Schema(description = "익스프레셔니즘(표현주의)")
  EXPRESSIONISM("익스프레셔니즘(표현주의)"),
  @Schema(description = "기괴·이상한")
  BIZARRE_OR_WEIRD("기괴·이상한"),

  @Schema(description = "파스텔톤")
  PASTEL_TONE("파스텔톤"),
  @Schema(description = "모노톤")
  MONOTONE("모노톤"),
  @Schema(description = "원색 대비")
  PRIMARY_COLOR_CONTRAST("원색 대비"),
  @Schema(description = "흑백")
  BLACK_AND_WHITE("흑백"),
  @Schema(description = "세피아톤")
  SEPIA_TONE("세피아톤"),
  @Schema(description = "메탈릭")
  METALLIC("메탈릭"),
  @Schema(description = "그라데이션")
  GRADATION("그라데이션"),
  @Schema(description = "강한 대비 채도")
  HIGH_CONTRAST_SATURATION("강한 대비 채도"),
  @Schema(description = "톤다운 컬러")
  TONE_DOWN_COLOR("톤다운 컬러");

  private final String ko;
}
