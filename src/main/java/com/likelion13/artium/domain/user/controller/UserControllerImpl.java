package com.likelion13.artium.domain.user.controller;

import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.service.UserService;
import com.likelion13.artium.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

  private final UserService userService;

  @Override
  public ResponseEntity<BaseResponse<SignUpResponse>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {

    SignUpResponse signUpResponse = userService.signUp(signUpRequest);

    return ResponseEntity.ok(BaseResponse.success("회원가입에 성공했습니다.", signUpResponse));
  }
}
