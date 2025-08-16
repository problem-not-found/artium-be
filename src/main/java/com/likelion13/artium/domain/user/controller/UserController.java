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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.CreatorResponse;
import com.likelion13.artium.domain.user.dto.response.LikeResponse;
import com.likelion13.artium.domain.user.dto.response.PreferenceResponse;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserContactResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.dto.response.UserSummaryResponse;
import com.likelion13.artium.domain.user.entity.Age;
import com.likelion13.artium.domain.user.entity.FormatPreference;
import com.likelion13.artium.domain.user.entity.Gender;
import com.likelion13.artium.domain.user.entity.MoodPreference;
import com.likelion13.artium.domain.user.entity.ThemePreference;
import com.likelion13.artium.global.page.response.PageResponse;
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

  @GetMapping("/check-code")
  @Operation(
      summary = "코드 중복 여부 확인",
      description =
          """
              사용자가 입력한 코드가 이미 존재하는지 여부를 반환합니다.
              true -> 중복되는 코드, 변경할 수 없음.
              false -> 중복되지 않는 코드, 변경 가능.
              """)
  ResponseEntity<BaseResponse<Boolean>> checkCodeDuplicated(
      @Parameter(description = "확인할 코드", example = "simonisnextdoor") @RequestParam String code);

  @GetMapping("/{id}/profile")
  @Operation(summary = "사용자 프로필 조회", description = "사용자의 프로필 사진, 닉네임을 조회합니다.")
  ResponseEntity<BaseResponse<UserSummaryResponse>> getUserProfile(
      @Parameter(description = "특정 유저 식별자") @PathVariable(value = "id") Long userId);

  @GetMapping("/{id}/contact")
  @Operation(summary = "사용자 연락 수단 조회", description = "사용자의 이메일, 인스타그램을 조회합니다.")
  ResponseEntity<BaseResponse<UserContactResponse>> getUserContact(
      @Parameter(description = "특정 유저 식별자") @PathVariable(value = "id") Long userId);

  @GetMapping("/{id}/creator")
  @Operation(summary = "크리에이터 정보 조회", description = "크리에이터의 정보를 조회합니다.")
  ResponseEntity<BaseResponse<CreatorResponse>> getCreatorInfo(
      @Parameter(description = "특정 유저 식별자") @PathVariable(value = "id") Long userId);

  @GetMapping("/likes")
  @Operation(summary = "사용자가 좋아요 했던 크리에이터 목록을 조회")
  ResponseEntity<BaseResponse<PageResponse<UserSummaryResponse>>> getLikes(
      @Parameter(description = "페이지 번호", example = "1") @RequestParam Integer pageNum,
      @Parameter(description = "페이지 크기", example = "3") @RequestParam Integer pageSize);

  @GetMapping("/preferences")
  @Operation(summary = "사용자 맞춤 취향 설정 조회", description = "사용자 맞춤 취향 설정을 조회합니다.")
  ResponseEntity<BaseResponse<PreferenceResponse>> getPreferences();

  @PutMapping("/user-info")
  @Operation(summary = "코드와 닉네임 변경", description = "현재 로그인된 사용자의 코드와 닉네임을 변경합니다.")
  ResponseEntity<BaseResponse<String>> updateUserInfo(
      @Parameter(description = "변경할 코드", example = "simonisnextdoor") @RequestParam String newCode,
      @Parameter(description = "변경할 닉네임", example = "아르티움") @RequestParam String newNickname);

  @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "프로필 사진 변경", description = "현재 로그인된 사용자의 프로필 사진을 변경합니다.")
  ResponseEntity<BaseResponse<String>> updateProfileImage(
      @Parameter(description = "새로운 프로필 사진") @RequestPart MultipartFile profileImage);

  @PutMapping("/preferences")
  @Operation(summary = "사용자 맞춤 취향 설정 변경", description = "사용자 맞춤 취향 설정을 변경합니다.")
  ResponseEntity<BaseResponse<String>> setPreferences(
      @Parameter(description = "성별 설정", example = "MALE") @RequestParam Gender gender,
      @Parameter(description = "연령대 설정", example = "TWENTIES") @RequestParam Age age,
      @Parameter(description = "주제 취향 설정") @RequestParam List<ThemePreference> themePreferences,
      @Parameter(description = "분위기 취향 설정") @RequestParam List<MoodPreference> moodPreferences,
      @Parameter(description = "형식 취향 설정") @RequestParam List<FormatPreference> formatPreferences);

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

  @PutMapping("/{piece-id}/approve/admin")
  @Operation(summary = "[관리자]등록 신청한 작품 승인", description = "사용자가 등록 신청한 작품을 승인합니다.")
  ResponseEntity<BaseResponse<String>> approvePiece(
      @Parameter(description = "작품 식별자", example = "1") @PathVariable("piece-id") Long pieceId);

  @PutMapping("/{piece-id}/reject/admin")
  @Operation(summary = "[관리자]등록 신청한 작품 거절", description = "사용자가 등록 신청한 작품을 거절합니다.")
  ResponseEntity<BaseResponse<String>> rejectPiece(
      @Parameter(description = "작품 식별자", example = "1") @PathVariable("piece-id") Long pieceId);
}
