/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
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
  @Column(name = "status", nullable = false)
  private Status status = Status.UNREGISTERED;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "piece", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PieceDetail> pieceDetails = new ArrayList<>();

  public void update(String title, String description, Boolean isPurchasable, Status status) {
    this.title = title;
    this.description = description;
    this.isPurchasable = isPurchasable;
    this.status = status;
  }

  public String updateImageUrl(String imageUrl) {
    String oldImageUrl = this.imageUrl;
    this.imageUrl = imageUrl;
    return oldImageUrl;
  }

  public void updatePieceDetails(List<PieceDetail> pieceDetails) {
    this.pieceDetails = pieceDetails;
  }

  public void addPieceDetail(PieceDetail pieceDetail) {
    this.pieceDetails.add(pieceDetail);
  }
}
