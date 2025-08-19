/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.controller;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.CreatorResponse;
import com.likelion13.artium.domain.user.dto.response.PreferenceResponse;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserContactResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.dto.response.UserLikeResponse;
import com.likelion13.artium.domain.user.dto.response.UserSummaryResponse;
import com.likelion13.artium.domain.user.entity.Age;
import com.likelion13.artium.domain.user.entity.FormatPreference;
import com.likelion13.artium.domain.user.entity.Gender;
import com.likelion13.artium.domain.user.entity.MoodPreference;
import com.likelion13.artium.domain.user.entity.ThemePreference;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.service.UserService;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.exception.PageErrorStatus;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

  private final UserService userService;

  @Override
  public ResponseEntity<BaseResponse<SignUpResponse>> signUp(
      @RequestPart("request") @Valid SignUpRequest signUpRequest,
      @RequestPart("image") MultipartFile image) {

    SignUpResponse signUpResponse = userService.signUp(signUpRequest, image);

    return ResponseEntity.ok(BaseResponse.success(201, "회원가입에 성공하였습니다.", signUpResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<UserLikeResponse>> createUserLike(@PathVariable Long id) {

    return ResponseEntity.ok(
        BaseResponse.success(201, "사용자 좋아요에 성공하였습니다.", userService.createUserLike(id)));
  }

  @Override
  public ResponseEntity<BaseResponse<UserDetailResponse>> getUserDetail() {

    return ResponseEntity.ok(BaseResponse.success(userService.getUserDetail()));
  }

  @Override
  public ResponseEntity<BaseResponse<Boolean>> checkCodeDuplicated(@RequestParam String code) {
    return ResponseEntity.ok(BaseResponse.success(userService.checkCodeDuplicated(code)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<UserSummaryResponse>>> getLikes(
      @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(200).body(BaseResponse.success(userService.getLikes(pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<PreferenceResponse>> getPreferences() {
    return ResponseEntity.status(200).body(BaseResponse.success(userService.getPreferences()));
  }

  @Override
  public ResponseEntity<BaseResponse<UserSummaryResponse>> getUserProfile(
      @PathVariable(value = "id") Long userId) {
    return ResponseEntity.status(200)
        .body(BaseResponse.success(userService.getUserProfile(userId)));
  }

  @Override
  public ResponseEntity<BaseResponse<PageResponse<UserSummaryResponse>>> getUserProfilesByCode(
      @RequestParam String code, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {

    Pageable pageable = validatePageable(pageNum, pageSize);

    return ResponseEntity.status(200).body(BaseResponse.success(userService.getUserProfilesByCode(code, pageable)));
  }

  @Override
  public ResponseEntity<BaseResponse<UserContactResponse>> getUserContact(
      @PathVariable(value = "id") Long userId) {
    return ResponseEntity.status(200)
        .body(BaseResponse.success(userService.getUserContact(userId)));
  }

  @Override
  public ResponseEntity<BaseResponse<CreatorResponse>> getCreatorInfo(
      @PathVariable(value = "id") Long userId) {
    return ResponseEntity.status(200)
        .body(BaseResponse.success(userService.getCreatorInfo(userId)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> updateUserInfo(
      @RequestParam String newCode, @RequestParam String newNickname) {
    return ResponseEntity.ok(
        BaseResponse.success(userService.updateUserInfo(newCode, newNickname)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> updateProfileImage(
      @RequestPart MultipartFile profileImage) {

    return ResponseEntity.ok(BaseResponse.success(userService.updateProfileImage(profileImage)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> setPreferences(
      @RequestParam Gender gender,
      @RequestParam Age age,
      @RequestParam List<ThemePreference> themePreferences,
      @RequestParam List<MoodPreference> moodPreferences,
      @RequestParam List<FormatPreference> formatPreferences) {
    if (themePreferences == null
        || themePreferences.isEmpty()
        || themePreferences.size() > 5
        || moodPreferences == null
        || moodPreferences.isEmpty()
        || moodPreferences.size() > 5
        || formatPreferences == null
        || formatPreferences.isEmpty()
        || formatPreferences.size() > 5) {
      throw new CustomException(UserErrorCode.INVALID_INPUT_REQUEST);
    }

    return ResponseEntity.status(200)
        .body(
            BaseResponse.success(
                userService.setPreferences(
                    gender, age, themePreferences, moodPreferences, formatPreferences)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deleteUser() {

    return ResponseEntity.ok(BaseResponse.success(userService.deleteUser()));
  }

  @Override
  public ResponseEntity<BaseResponse<UserLikeResponse>> deleteUserLike(@PathVariable Long id) {

    return ResponseEntity.ok(BaseResponse.success(userService.deleteUserLike(id)));
  }

  @Override
  public ResponseEntity<BaseResponse<List<UserDetailResponse>>> getAllUsers() {

    List<UserDetailResponse> userDetailResponses = userService.getAllUsers();

    return ResponseEntity.ok(BaseResponse.success(userDetailResponses));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> approvePiece(@PathVariable("piece-id") Long pieceId) {

    return ResponseEntity.status(200).body(BaseResponse.success(userService.approvePiece(pieceId)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> rejectPiece(@PathVariable("piece-id") Long pieceId) {

    return ResponseEntity.status(200).body(BaseResponse.success(userService.rejectPiece(pieceId)));
  }

  private Pageable validatePageable(Integer pageNum, Integer pageSize) {
    if (pageNum < 1) {
      throw new CustomException(PageErrorStatus.PAGE_NOT_FOUND);
    }
    if (pageSize < 1) {
      throw new CustomException(PageErrorStatus.PAGE_SIZE_ERROR);
    }

    return PageRequest.of(pageNum - 1, pageSize);
  }
}
