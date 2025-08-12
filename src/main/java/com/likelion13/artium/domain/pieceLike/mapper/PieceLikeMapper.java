/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.pieceLike.dto.response.PieceLikeResponse;

@Component
public class PieceLikeMapper {

  public PieceLikeResponse toPieceLikeResponse(Long pieceId, Boolean isLike) {
    return PieceLikeResponse.builder().pieceId(pieceId).isLike(isLike).build();
  }
}
