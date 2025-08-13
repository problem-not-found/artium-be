package com.likelion13.artium.domain.exhibition.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ExhibitionRole {
  @Schema(description = "주최자")
  ORGANIZER,
  @Schema(description = "참여자")
  PARTICIPANT
}