/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.dto.response.UserLikeResponse;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.global.exception.CustomException;

/**
 * 사용자 관련 주요 기능을 제공하는 서비스 인터페이스입니다.
 *
 * <p>주요 기능:
 *
 * <ul>
 *   <li>회원가입 처리
 *   <li>현재 인증된 사용자 조회
 * </ul>
 */
public interface UserService {

  /**
   * 회원가입 요청을 처리합니다.
   *
   * <p>회원가입 시 중복된 아이디가 존재하면 예외를 발생시키며, 비밀번호는 인코딩되어 저장됩니다.
   *
   * @param request 회원가입 요청 데이터 (아이디, 비밀번호, 닉네임 등)
   * @param image 회원 프로필 이미지 파일
   * @return 회원가입 성공 시 생성된 사용자 정보를 담은 응답 DTO
   * @throws CustomException 중복된 아이디 등 가입 불가 사유 발생 시
   */
  SignUpResponse signUp(SignUpRequest request, MultipartFile image);

  /**
   * 사용자 좋아요(Like)를 생성합니다.
   *
   * @param id 좋아요를 생성할 대상 사용자 ID
   * @return 생성된 좋아요 정보를 담은 {@link UserLikeResponse}
   */
  UserLikeResponse createUserLike(Long id);

  /**
   * 현재 인증된 사용자의 정보를 조회합니다.
   *
   * <p>인증 정보가 없거나 유효하지 않으면 예외를 던지며, OAuth2 로그인과 자체 로그인 모두 지원합니다.
   *
   * @return 현재 인증된 {@link User} 엔티티
   * @throws CustomException 인증 실패, 사용자 미존재 시 발생
   */
  User getCurrentUser();

  /**
   * 현재 인증된 사용자의 상세 정보를 조회합니다.
   *
   * @return {@link UserDetailResponse} 현재 인증된 사용자의 상세 정보
   */
  UserDetailResponse getUserDetail();

  /**
   * 닉네임 사용 가능 여부를 확인합니다.
   *
   * @param nickname 확인할 닉네임 문자열
   * @return true이면 사용 가능, false면 중복
   */
  Boolean checkNicknameDuplicated(String nickname);

  /**
   * 사용자의 닉네임을 변경합니다.
   *
   * @param newNickname 변경할 새로운 닉네임
   * @return 변경 완료 메시지 문자열
   * @throws CustomException 닉네임 중복 등 변경 불가 시 발생
   */
  String updateNickname(String newNickname);

  /**
   * 사용자의 프로필 이미지를 변경합니다.
   *
   * @param profileImage 변경할 프로필 이미지 파일
   * @return 변경 완료 메시지 문자열
   */
  String updateProfileImage(MultipartFile profileImage);

  /**
   * 사용자를 탈퇴 처리합니다.
   *
   * @return 탈퇴 완료 메시지 문자열
   * @throws CustomException 탈퇴 불가 시 발생
   */
  String deleteUser();

  /**
   * 사용자 좋아요(Like)를 삭제합니다.
   *
   * @param id 좋아요를 삭제할 대상 사용자 ID
   * @return 삭제된 좋아요 정보를 담은 {@link UserLikeResponse}
   */
  UserLikeResponse deleteUserLike(Long id);

  /**
   * 모든 사용자의 상세 정보를 조회합니다.
   *
   * @return 모든 사용자의 {@link UserDetailResponse} 목록
   */
  List<UserDetailResponse> getAllUsers();

  /**
   * 사용자가 등록 신청한 작품을 승인합니다.
   *
   * @param pieceId 작품 식별자
   * @return 승인 완료 문자열
   */
  String approvePiece(Long pieceId);

  /**
   * 사용자가 등록 신청한 작품을 거절합니다.
   *
   * @param pieceId 작품 식별자
   * @return 거절 완료 문자열
   */
  String rejectPiece(Long pieceId);
}
