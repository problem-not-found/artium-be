/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.service;

import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.global.exception.CustomException;

/** 사용자 관련 주요 기능을 제공하는 서비스 인터페이스입니다. */
public interface UserService {

  /**
   * 회원가입 요청을 처리합니다.
   *
   * <p>회원가입 시 중복된 아이디가 존재하면 예외를 발생시키며, 비밀번호는 인코딩되어 저장됩니다.
   *
   * @param request 회원가입 요청 데이터 (아이디, 비밀번호, 닉네임 등)
   * @return 회원가입 성공 시 생성된 사용자 정보를 담은 응답 DTO
   * @throws CustomException 중복된 아이디 등 가입 불가 사유 발생 시
   */
  SignUpResponse signUp(SignUpRequest request, MultipartFile image);

  /**
   * 현재 인증된 사용자의 정보를 조회합니다.
   *
   * <p>인증 정보가 없거나 유효하지 않으면 예외를 던지며, OAuth2 로그인과 자체 로그인 모두 지원합니다.
   *
   * @return 현재 인증된 {@link User} 엔티티
   * @throws CustomException 인증 실패, 사용자 미존재 시 발생
   */
  User getCurrentUser();
}
