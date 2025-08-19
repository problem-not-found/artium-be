/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.mapping;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
    name = "exhibition_piece",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_exhibition_piece_exhibition_piece",
          columnNames = {"exhibition_id", "piece_id"})
    },
    indexes = {
      @Index(name = "idx_exhibition_piece_exhibition_id", columnList = "exhibition_id"),
      @Index(name = "idx_exhibition_piece_piece_id", columnList = "piece_id")
    })
public class ExhibitionPiece extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exhibition_id", nullable = false)
  private Exhibition exhibition;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "piece_id", nullable = false)
  private Piece piece;
}
