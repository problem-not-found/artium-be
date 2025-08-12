/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.service.UserService;
import com.likelion13.artium.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "User 관리 API")
@RequestMapping("/api/users")
public interface UserController {

  @PostMapping("/sign-up")
  @Operation(summary = "회원가입", description = "서비스 자체 회원을 위한 회원가입을 처리합니다.")
  ResponseEntity<BaseResponse<SignUpResponse>> signUp(@RequestBody @Valid SignUpRequest signUpRequest);
}
