/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "전시 주제 취향 Enum")
@Getter
@RequiredArgsConstructor
public enum ThemePreference {
  @Schema(description = "불교미술")
  BUDDHIST_ART("불교미술"),
  @Schema(description = "고대 유물")
  ANCIENT_ARTIFACTS("고대 유물"),
  @Schema(description = "근대사")
  MODERN_HISTORY("근대사"),
  @Schema(description = "전통 민속")
  TRADITIONAL_FOLK("전통 민속"),
  @Schema(description = "궁중 문화")
  ROYAL_CULTURE("궁중 문화"),
  @Schema(description = "신화와 전설")
  MYTHS_AND_LEGENDS("신화와 전설"),
  @Schema(description = "종교 예술")
  RELIGIOUS_ART("종교 예술"),
  @Schema(description = "세계 문화유산")
  WORLD_HERITAGE("세계 문화유산"),
  @Schema(description = "지역 문화")
  REGIONAL_CULTURE("지역 문화"),
  @Schema(description = "전통 의상")
  TRADITIONAL_COSTUME("전통 의상"),
  @Schema(description = "무속 신앙")
  SHAMANISM("무속 신앙"),
  @Schema(description = "문학·시에서 영감받은 작품")
  LITERATURE_AND_POETRY_INSPIRED("문학·시에서 영감받은 작품"),
  @Schema(description = "바다")
  SEA("바다"),
  @Schema(description = "숲")
  FOREST("숲"),
  @Schema(description = "사막")
  DESERT("사막"),
  @Schema(description = "설원")
  SNOWFIELD("설원"),
  @Schema(description = "폭포")
  WATERFALL("폭포"),
  @Schema(description = "하늘과 구름")
  SKY_AND_CLOUDS("하늘과 구름"),
  @Schema(description = "꽃과 식물")
  FLOWERS_AND_PLANTS("꽃과 식물"),
  @Schema(description = "동물")
  ANIMALS("동물"),
  @Schema(description = "곤충")
  INSECTS("곤충"),
  @Schema(description = "기후 변화")
  CLIMATE_CHANGE("기후 변화"),
  @Schema(description = "계절(봄·여름·가을·겨울)")
  SEASONS("계절(봄·여름·가을·겨울)"),
  @Schema(description = "별·천체")
  STARS_AND_CELESTIAL("별·천체"),

  @Schema(description = "초상화")
  PORTRAIT("초상화"),
  @Schema(description = "인물 사진")
  PEOPLE_PHOTOGRAPHY("인물 사진"),
  @Schema(description = "도시 인물")
  URBAN_PORTRAIT("도시 인물"),
  @Schema(description = "일상 기록")
  DAILY_LIFE("일상 기록"),
  @Schema(description = "사회 운동")
  SOCIAL_MOVEMENT("사회 운동"),
  @Schema(description = "노동과 산업")
  LABOR_AND_INDUSTRY("노동과 산업"),
  @Schema(description = "가족·관계")
  FAMILY_RELATIONSHIP("가족·관계"),
  @Schema(description = "자화상")
  SELF_PORTRAIT("자화상"),
  @Schema(description = "패션")
  FASHION("패션"),
  @Schema(description = "유명 인물")
  CELEBRITY("유명 인물"),
  @Schema(description = "기하학")
  GEOMETRY("기하학"),
  @Schema(description = "패턴")
  PATTERN("패턴"),
  @Schema(description = "무한 반복")
  INFINITE_LOOP("무한 반복"),
  @Schema(description = "색채 실험")
  COLOR_EXPERIMENT("색채 실험"),
  @Schema(description = "시간의 흐름")
  PASSAGE_OF_TIME("시간의 흐름"),
  @Schema(description = "데이터 기반 시각화")
  DATA_VISUALIZATION("데이터 기반 시각화"),
  @Schema(description = "상징과 은유")
  SYMBOL_AND_METAPHOR("상징과 은유"),
  @Schema(description = "심리학/마음")
  PSYCHOLOGY_AND_MIND("심리학/마음"),
  @Schema(description = "관념적 풍경")
  CONCEPTUAL_LANDSCAPE("관념적 풍경"),
  @Schema(description = "음식")
  FOOD("음식"),
  @Schema(description = "여행지")
  TRAVEL_DESTINATION("여행지"),
  @Schema(description = "판타지 세계")
  FANTASY_WORLD("판타지 세계"),
  @Schema(description = "과학과 기술")
  SCIENCE_AND_TECHNOLOGY("과학과 기술"),
  @Schema(description = "미래 도시")
  FUTURE_CITY("미래 도시"),
  @Schema(description = "로봇")
  ROBOT("로봇"),
  @Schema(description = "게임·서브컬처")
  GAME_AND_SUBCULTURE("게임·서브컬처"),
  @Schema(description = "스포츠")
  SPORTS("스포츠");

  private final String ko;
}
