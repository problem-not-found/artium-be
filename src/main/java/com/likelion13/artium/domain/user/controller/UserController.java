/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.LikeResponse;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "사용자", description = "사용자 관련 API")
@RequestMapping("/api/users")
public interface UserController {

  @PostMapping(value = "/sign-up", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "회원가입", description = "서비스 자체 회원을 위한 회원가입을 처리합니다.")
  ResponseEntity<BaseResponse<SignUpResponse>> signUp(
      @Parameter(
              description = "회원가입 정보",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @RequestPart(value = "request")
          @Valid
          SignUpRequest signUpRequest,
      @Parameter(
              description = "프로필 이미지",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestPart(value = "image")
          MultipartFile image);

  @PostMapping("/like")
  @Operation(summary = "사용자 좋아요", description = "좋아요 할 사용자의 식별자를 요청 받아 좋아요를 등록합니다.")
  ResponseEntity<BaseResponse<LikeResponse>> createUserLike(
      @Parameter(description = "좋아요 할 사용자 식별자", example = "1") @RequestParam Long userId);

  @GetMapping
  @Operation(summary = "사용자 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
  ResponseEntity<BaseResponse<UserDetailResponse>> getUserDetail();

  @GetMapping("/check")
  @Operation(
      summary = "닉네임 중복 여부 확인",
      description =
          """
              사용자가 입력한 닉네임이 이미 존재하는지 여부를 반환합니다.
              true -> 중복되는 닉네임, 변경할 수 없음.
              false -> 중복되지 않는 닉네임, 변경 가능.
              """)
  ResponseEntity<BaseResponse<Boolean>> checkNicknameDuplicated(
      @Parameter(description = "확인할 닉네임", example = "아르티움") @RequestParam String nickname);

  @PutMapping("/nickname")
  @Operation(summary = "닉네임 변경", description = "현재 로그인된 사용자의 닉네임을 변경합니다.")
  ResponseEntity<BaseResponse<String>> updateNickname(
      @Parameter(description = "변경할 닉네임", example = "아르티움") @RequestParam String newNickname);

  @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "프로필 사진 변경", description = "현재 로그인된 사용자의 프로필 사진을 변경합니다.")
  ResponseEntity<BaseResponse<String>> updateProfileImage(
      @Parameter(description = "새로운 프로필 사진") @RequestPart MultipartFile profileImage);

  @DeleteMapping
  @Operation(summary = "사용자 탈퇴", description = "현재 로그인된 사용자를 Soft Delete 처리합니다.")
  ResponseEntity<BaseResponse<String>> deleteUser();

  @DeleteMapping("/like")
  @Operation(summary = "사용자 좋아요 취소", description = "좋아요 했던 사용자의 식별자를 요청 받아 좋아요를 취소합니다.")
  ResponseEntity<BaseResponse<LikeResponse>> deleteUserLike(
      @Parameter(description = "좋아요 했던 사용자 식별자", example = "1") @RequestParam Long userId);

  @GetMapping("/devs")
  @Operation(summary = "[개발자]사용자 전체 조회", description = "스웨거를 사용해 전체 사용자를 조회합니다.")
  ResponseEntity<BaseResponse<List<UserDetailResponse>>> getAllUsers();

  @PutMapping("/{piece-id}/approve")
  @Operation(summary = "[관리자]등록 신청한 작품 승인", description = "사용자가 등록 신청한 작품을 승인합니다.")
  ResponseEntity<BaseResponse<String>> approvePiece(
      @Parameter(description = "작품 식별자", example = "1") Long pieceId);

  @PutMapping("/{piece-id}/reject")
  @Operation(summary = "[관리자]등록 신청한 작품 거절", description = "사용자가 등록 신청한 작품을 거절합니다.")
  ResponseEntity<BaseResponse<String>> rejectPiece(
      @Parameter(description = "작품 식별자", example = "1") Long pieceId);
}
