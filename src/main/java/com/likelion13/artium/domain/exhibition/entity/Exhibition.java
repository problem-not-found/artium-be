/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.entity;

import java.time.LocalDate;
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

import com.likelion13.artium.domain.exhibition.mapping.ExhibitionLike;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;
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
@Table(name = "exhibition")
public class Exhibition extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "thumbnail_image_url")
  private String thumbnailImageUrl;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "address")
  private String address;

  @Column(name = "offline_description")
  private String offlineDescription;

  @Column(name = "account_number")
  private String accountNumber;

  @Column(name = "bank_name")
  @Enumerated(EnumType.STRING)
  private BankName bankName;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private ExhibitionStatus exhibitionStatus;

  @Column(name = "fill_all")
  @Builder.Default
  private Boolean fillAll = Boolean.FALSE;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ExhibitionParticipant> exhibitionParticipants = new ArrayList<>();

  @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ExhibitionLike> exhibitionLikes = new ArrayList<>();

  public void update(Exhibition exhibition) {
    this.thumbnailImageUrl = exhibition.getThumbnailImageUrl();
    this.title = exhibition.getTitle();
    this.description = exhibition.getDescription();
    this.startDate = exhibition.getStartDate();
    this.endDate = exhibition.getEndDate();
    this.address = exhibition.getAddress();
    this.offlineDescription = exhibition.getOfflineDescription();
    this.accountNumber = exhibition.getAccountNumber();
    this.bankName = exhibition.getBankName();
    this.exhibitionStatus = exhibition.getExhibitionStatus();
    this.fillAll = exhibition.getFillAll();
    this.user = exhibition.getUser();
  }
}
