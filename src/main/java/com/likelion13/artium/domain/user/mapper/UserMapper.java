/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.mapping.ExhibitionLike;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;
import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.dto.response.UserLikeResponse;
import com.likelion13.artium.domain.user.entity.Role;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.mapping.UserLike;

@Component
public class UserMapper {

  public User toUser(SignUpRequest request, String encodedPassword, String imageUrl) {
    return User.builder()
        .username(request.getUsername())
        .password(encodedPassword)
        .nickname(request.getNickname())
        .profileImageUrl(imageUrl)
        .role(Role.USER)
        .isDeleted(false)
        .build();
  }

  public UserLike toUserLike(User currentUser, User targetUser) {
    return UserLike.builder().liker(currentUser).liked(targetUser).build();
  }

  public SignUpResponse toSignUpResponse(User user) {
    return SignUpResponse.builder().userId(user.getId()).username(user.getUsername()).build();
  }

  public UserLikeResponse toUserLikeResponse(String currentUser, String targetUser) {
    return UserLikeResponse.builder()
        .currentUserNickname(currentUser)
        .targetUserNickname(targetUser)
        .build();
  }

  public UserDetailResponse toUserDetailResponse(User user) {
    return UserDetailResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .nickname(user.getNickname())
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
}
