/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import com.likelion13.artium.domain.pieceLike.entity.PieceLike;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "piece")
public class Piece extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "is_purchasable", nullable = false)
  private Boolean isPurchasable;

  @Enumerated(EnumType.STRING)
  @Column(name = "save_status", nullable = false)
  private SaveStatus saveStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "progress_status")
  @Builder.Default
  private ProgressStatus progressStatus = ProgressStatus.WAITING;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exhibition_id", nullable = false)
  private Exhibition exhibition;

  @OneToMany(mappedBy = "piece", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PieceDetail> pieceDetails = new ArrayList<>();

  @OneToMany(mappedBy = "piece", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PieceLike> pieceLikes = new ArrayList<>();

  public void update(
      String title, String description, Boolean isPurchasable, SaveStatus saveStatus) {
    this.title = title;
    this.description = description;
    this.isPurchasable = isPurchasable;
    this.saveStatus = saveStatus;
  }

  public void updateProgressStatus(ProgressStatus progressStatus) {
    this.progressStatus = progressStatus;
  }

  public String updateImageUrl(String imageUrl) {
    String oldImageUrl = this.imageUrl;
    this.imageUrl = imageUrl;
    return oldImageUrl;
  }

  public void addPieceDetail(PieceDetail pieceDetail) {
    if (pieceDetail == null) return;
    if (this.pieceDetails == null) pieceDetails = new ArrayList<>();
    pieceDetails.add(pieceDetail);
  }
}
