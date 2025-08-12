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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.likelion13.artium.domain.exhibition.mapping.ExhibitionUser;

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
public class Exhibition {

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

  @Column(name = "account_number")
  private String accountNumber;

  @Column(name = "bank_name")
  private BankName bankName;

  @Column(name = "status")
  private ExhibitionStatus exhibitionStatus;

  @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ExhibitionUser> exhibitionUsers = new ArrayList<>();
}
