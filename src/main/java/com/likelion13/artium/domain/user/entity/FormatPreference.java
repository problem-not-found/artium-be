/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "전시 형식 취향 Enum")
@Getter
@RequiredArgsConstructor
public enum FormatPreference {
  @Schema(description = "유화")
  OIL_PAINTING("유화"),
  @Schema(description = "수채화")
  WATERCOLOR("수채화"),
  @Schema(description = "아크릴화")
  ACRYLIC_PAINTING("아크릴화"),
  @Schema(description = "먹화")
  INK_PAINTING("먹화"),
  @Schema(description = "콜라주")
  COLLAGE("콜라주"),
  @Schema(description = "스케치")
  SKETCH("스케치"),
  @Schema(description = "혼합매체(Mixed Media)")
  MIXED_MEDIA("혼합매체(Mixed Media)"),
  @Schema(description = "디지털 페인팅")
  DIGITAL_PAINTING("디지털 페인팅"),

  @Schema(description = "목조각")
  WOOD_SCULPTURE("목조각"),
  @Schema(description = "석조각")
  STONE_SCULPTURE("석조각"),
  @Schema(description = "금속 조형물")
  METAL_SCULPTURE("금속 조형물"),
  @Schema(description = "대형 설치미술")
  LARGE_INSTALLATION("대형 설치미술"),
  @Schema(description = "환경 설치")
  ENVIRONMENTAL_INSTALLATION("환경 설치"),
  @Schema(description = "재활용 소재 아트")
  RECYCLED_MATERIAL_ART("재활용 소재 아트"),

  @Schema(description = "필름 사진")
  FILM_PHOTOGRAPHY("필름 사진"),
  @Schema(description = "디지털 사진")
  DIGITAL_PHOTOGRAPHY("디지털 사진"),
  @Schema(description = "다큐멘터리 영상")
  DOCUMENTARY_VIDEO("다큐멘터리 영상"),
  @Schema(description = "3D 영상")
  THREE_D_VIDEO("3D 영상"),
  @Schema(description = "애니메이션")
  ANIMATION("애니메이션"),
  @Schema(description = "스톱모션")
  STOP_MOTION("스톱모션"),
  @Schema(description = "시네마그래프")
  CINEMAGRAPH("시네마그래프"),

  @Schema(description = "춤")
  DANCE("춤"),
  @Schema(description = "연극")
  THEATER("연극"),
  @Schema(description = "음악 퍼포먼스")
  MUSIC_PERFORMANCE("음악 퍼포먼스"),
  @Schema(description = "실시간 라이브 아트")
  LIVE_ART("실시간 라이브 아트"),
  @Schema(description = "플래시몹")
  FLASH_MOB("플래시몹"),

  @Schema(description = "AR/VR")
  AR_VR("AR/VR"),
  @Schema(description = "인터랙티브 아트")
  INTERACTIVE_ART("인터랙티브 아트"),
  @Schema(description = "AI 생성 아트")
  AI_GENERATED_ART("AI 생성 아트"),
  @Schema(description = "데이터 아트")
  DATA_ART("데이터 아트"),
  @Schema(description = "프로젝션 매핑")
  PROJECTION_MAPPING("프로젝션 매핑"),
  @Schema(description = "NFT 아트")
  NFT_ART("NFT 아트"),
  @Schema(description = "홀로그램")
  HOLOGRAM("홀로그램"),

  @Schema(description = "도자기")
  CERAMICS("도자기"),
  @Schema(description = "유리공예")
  GLASS_ART("유리공예"),
  @Schema(description = "섬유·패브릭 아트")
  TEXTILE_ART("섬유·패브릭 아트"),
  @Schema(description = "가구 디자인")
  FURNITURE_DESIGN("가구 디자인"),
  @Schema(description = "주얼리")
  JEWELRY("주얼리"),
  @Schema(description = "패션 아트")
  FASHION_ART("패션 아트"),
  @Schema(description = "건축 모형")
  ARCHITECTURAL_MODEL("건축 모형"),
  @Schema(description = "도시 설치물")
  URBAN_INSTALLATION("도시 설치물"),
  @Schema(description = "게임 아트")
  GAME_ART("게임 아트"),
  @Schema(description = "실험적 사운드 아트")
  EXPERIMENTAL_SOUND_ART("실험적 사운드 아트");

  private final String ko;
}
