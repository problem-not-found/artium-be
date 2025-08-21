/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.mapping;

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

import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
    name = "user_like",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_user_like_liked_liker",
          columnNames = {"liked_id", "liker_id"})
    },
    indexes = {
      @Index(name = "idx_user_like_liked_id", columnList = "liked_id"),
      @Index(name = "idx_user_like_liker_id", columnList = "liker_id")
    })
public class UserLike extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 좋아요 보낸 사람
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "liked_id", nullable = false)
  private User liked;

  // 좋아요 받은 사람
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "liker_id", nullable = false)
  private User liker;
}
