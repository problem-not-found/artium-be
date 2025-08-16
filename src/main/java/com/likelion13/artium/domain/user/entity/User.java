/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionLike;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceLike.entity.PieceLike;
import com.likelion13.artium.domain.review.entity.Review;
import com.likelion13.artium.domain.user.mapping.UserLike;
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
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @JsonIgnore
  @Column(name = "password")
  private String password;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "code", unique = true)
  private String code;

  @Column(name = "gender")
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(name = "age")
  @Enumerated(EnumType.STRING)
  private Age age;

  @ElementCollection
  @CollectionTable(name = "user_theme_preferences", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "theme_preference")
  @Enumerated(EnumType.STRING)
  private List<ThemePreference> themePreferences = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "user_mood_preferences", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "mood_preference")
  @Enumerated(EnumType.STRING)
  private List<MoodPreference> moodPreferences = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "user_format_preferences", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "format_preference")
  @Enumerated(EnumType.STRING)
  private List<FormatPreference> formatPreferences = new ArrayList<>();

  @Column(name = "profile_image_url", nullable = false)
  private String profileImageUrl;

  @Column(name = "introduction")
  private String introduction;

  @Column(name = "email")
  private String email;

  @Column(name = "instagram")
  private String instagram;

  @Column(name = "role", nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Role role = Role.ROLE_USER;

  @Column(name = "is_deleted", nullable = false)
  @Builder.Default
  private boolean isDeleted = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @Builder.Default
  private List<Piece> pieces = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PieceLike> pieceLikes = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ExhibitionParticipant> exhibitionParticipants = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ExhibitionLike> exhibitionLikes = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Review> reviews = new ArrayList<>();

  // 내가 좋아요 한 사용자
  @OneToMany(mappedBy = "liker", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<UserLike> likedUsers = new ArrayList<>();

  // 나를 좋아요 한 사용자
  @OneToMany(mappedBy = "liked", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<UserLike> likedByUsers = new ArrayList<>();

  public static User fromOAuth(String email, String nickname, String profileImageUrl) {
    return User.builder()
        .username(email)
        .password(UUID.randomUUID().toString())
        .nickname(nickname)
        .profileImageUrl(profileImageUrl)
        .role(Role.ROLE_USER)
        .build();
  }

  public void updateUserInfo(String newCode, String newNickname) {
    this.code = newCode;
    this.nickname = newNickname;
  }

  public void updateProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public void updatePreferences(
      Gender gender,
      Age age,
      List<ThemePreference> themePreferences,
      List<MoodPreference> moodPreferences,
      List<FormatPreference> formatPreferences) {
    this.gender = gender;
    this.age = age;
    this.themePreferences.clear();
    if (themePreferences != null) {
      this.themePreferences.addAll(themePreferences);
    }
    this.moodPreferences.clear();
    if (moodPreferences != null) {
      this.moodPreferences.addAll(moodPreferences);
    }
    this.formatPreferences.clear();
    if (formatPreferences != null) {
      this.formatPreferences.addAll(formatPreferences);
    }
  }

  public void softDelete() {
    this.isDeleted = true;
    this.deletedAt = LocalDateTime.now();
  }
}
