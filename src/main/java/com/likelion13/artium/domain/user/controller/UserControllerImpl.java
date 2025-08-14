/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.LikeResponse;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.service.UserService;
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
  public ResponseEntity<BaseResponse<LikeResponse>> createUserLike(@RequestParam Long userId) {

    return ResponseEntity.ok(
        BaseResponse.success(201, "사용자 좋아요에 성공하였습니다.", userService.createUserLike(userId)));
  }

  @Override
  public ResponseEntity<BaseResponse<UserDetailResponse>> getUserDetail() {

    return ResponseEntity.ok(BaseResponse.success(userService.getUserDetail()));
  }

  @Override
  public ResponseEntity<BaseResponse<Boolean>> checkNicknameDuplicated(
      @RequestParam String nickname) {
    return ResponseEntity.ok(BaseResponse.success(userService.checkNicknameDuplicated(nickname)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> updateNickname(@RequestParam String newNickname) {
    return ResponseEntity.ok(BaseResponse.success(userService.updateNickname(newNickname)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> updateProfileImage(
      @RequestPart MultipartFile profileImage) {

    return ResponseEntity.ok(BaseResponse.success(userService.updateProfileImage(profileImage)));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> deleteUser() {

    return ResponseEntity.ok(BaseResponse.success(userService.deleteUser()));
  }

  @Override
  public ResponseEntity<BaseResponse<LikeResponse>> deleteUserLike(@RequestParam Long userId) {

    return ResponseEntity.ok(BaseResponse.success(userService.deleteUserLike(userId)));
  }

  @Override
  public ResponseEntity<BaseResponse<List<UserDetailResponse>>> getAllUsers() {

    List<UserDetailResponse> userDetailResponses = userService.getAllUsers();

    return ResponseEntity.ok(BaseResponse.success(userDetailResponses));
  }

    @Override
    public ResponseEntity<BaseResponse<String>> approvePiece(Long pieceId) {

        return ResponseEntity.status(200).body(BaseResponse.success(userService.approvePiece(pieceId)));
    }

    @Override
    public ResponseEntity<BaseResponse<String>> rejectPiece(Long pieceId) {

        return ResponseEntity.status(200).body(BaseResponse.success(userService.rejectPiece(pieceId)));
    }
}
