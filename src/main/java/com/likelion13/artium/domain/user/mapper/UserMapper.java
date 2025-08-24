/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.mapping.ExhibitionLike;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;
import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.CreatorFeedResponse;
import com.likelion13.artium.domain.user.dto.response.CreatorResponse;
import com.likelion13.artium.domain.user.dto.response.PreferenceResponse;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserContactResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.dto.response.UserLikeResponse;
import com.likelion13.artium.domain.user.dto.response.UserParticipateResponse;
import com.likelion13.artium.domain.user.dto.response.UserResponse;
import com.likelion13.artium.domain.user.dto.response.UserSummaryResponse;
import com.likelion13.artium.domain.user.entity.FormatPreference;
import com.likelion13.artium.domain.user.entity.MoodPreference;
import com.likelion13.artium.domain.user.entity.Role;
import com.likelion13.artium.domain.user.entity.ThemePreference;
import com.likelion13.artium.domain.user.entity.User;

@Component
public class UserMapper {

  public User toUser(SignUpRequest request, String encodedPassword, String imageUrl) {
    return User.builder()
        .username(request.getUsername())
        .password(encodedPassword)
        .nickname(request.getNickname())
        .code(request.getCode())
        .profileImageUrl(imageUrl)
        .role(Role.ROLE_USER)
        .isDeleted(false)
        .build();
  }

  public SignUpResponse toSignUpResponse(User user) {
    return SignUpResponse.builder().userId(user.getId()).username(user.getUsername()).build();
  }

  public UserLikeResponse toUserLikeResponse(String currentUserCode, String targetUserCode) {
    return UserLikeResponse.builder()
        .currentUserCode(currentUserCode)
        .targetUserCode(targetUserCode)
        .build();
  }

  public UserDetailResponse toUserDetailResponse(User user) {
    return UserDetailResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .nickname(user.getNickname())
        .code(user.getCode())
        .profileImageUrl(user.getProfileImageUrl())
        .exhibitionParticipantIds(
            user.getExhibitionParticipants().stream()
                .map(ExhibitionParticipant::getId)
                .collect(Collectors.toList()))
        .likedUsersIds(
            user.getLikedUsers().stream()
                .map(userLike -> userLike.getLiked().getId())
                .collect(Collectors.toList()))
        .likedByUsersIds(
            user.getLikedByUsers().stream()
                .map(userLike -> userLike.getLiker().getId())
                .collect(Collectors.toList()))
        .exhibitionLikeIds(
            user.getExhibitionLikes().stream()
                .map(ExhibitionLike::getId)
                .collect(Collectors.toList()))
        .build();
  }

  public UserResponse toUserResponse(User user) {
    return UserResponse.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .code(user.getCode())
        .introduction(user.getIntroduction())
        .profileImageUrl(user.getProfileImageUrl())
        .build();
  }

  public UserSummaryResponse toUserSummaryResponse(User user) {
    return UserSummaryResponse.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .code(user.getCode())
        .profileImageUrl(user.getProfileImageUrl())
        .build();
  }

  public UserContactResponse toUserContactResponse(User user) {
    return UserContactResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .instagram(user.getInstagram())
        .build();
  }

  public CreatorResponse toCreatorResponse(User user, Boolean isLike) {
    return CreatorResponse.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .code(user.getCode())
        .profileImageUrl(user.getProfileImageUrl())
        .introduction(user.getIntroduction())
        .isLike(isLike)
        .build();
  }

  public CreatorFeedResponse toCreatorFeedResponse(User user, Boolean isLike) {
    List<String> pieceImageUrls = new ArrayList<>();
    user.getPieces()
        .forEach(
            piece -> {
              if (piece.getProgressStatus() != ProgressStatus.WAITING
                  && piece.getProgressStatus() != ProgressStatus.UNREGISTERED)
                pieceImageUrls.add(piece.getImageUrl());
            });
    return CreatorFeedResponse.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .profileImageUrl(user.getProfileImageUrl())
        .code(user.getCode())
        .pieceImageUrls(pieceImageUrls.stream().limit(2).toList())
        .isLike(isLike)
        .build();
  }

  public PreferenceResponse toPreferenceResponse(User user) {
    return PreferenceResponse.builder()
        .userId(user.getId())
        .age(user.getAge() != null ? user.getAge().getKo() : null)
        .gender(user.getGender() != null ? user.getGender().getKo() : null)
        .themePreferences(user.getThemePreferences().stream().map(ThemePreference::getKo).toList())
        .moodPreferences(user.getMoodPreferences().stream().map(MoodPreference::getKo).toList())
        .formatPreferences(
            user.getFormatPreferences().stream().map(FormatPreference::getKo).toList())
        .build();
  }

  public UserParticipateResponse toUserParticipateResponse(
      ExhibitionParticipant exhibitionParticipant) {
    return UserParticipateResponse.builder()
        .exhibitionId(exhibitionParticipant.getExhibition().getId())
        .thumbnailImageUrl(exhibitionParticipant.getExhibition().getThumbnailImageUrl())
        .title(exhibitionParticipant.getExhibition().getTitle())
        .build();
  }
}
