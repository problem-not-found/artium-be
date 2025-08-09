/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.entity;

import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
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

import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.global.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "is_purchasable", nullable = false)
  private Boolean isPurchasable;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "piece", cascade = CascadeType.ALL)
  private List<PieceDetail> pieceDetails = new ArrayList<>();

  public void update(String title, String description, Boolean isPurchasable) {
    this.title = title;
    this.description = description;
    this.isPurchasable = isPurchasable;
  }
}
