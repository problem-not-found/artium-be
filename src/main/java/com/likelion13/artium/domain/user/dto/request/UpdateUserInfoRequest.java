package com.likelion13.artium.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "UpdateInfoRequest DTO", description = "사용자 간단 정보 수정을 위한 데이터 전송")
public class UpdateInfoRequest {

}
