/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceDetail.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.pieceDetail.dto.response.PieceDetailResponse;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import com.likelion13.artium.domain.pieceDetail.mapper.PieceDetailMapper;
import com.likelion13.artium.domain.pieceDetail.repository.PieceDetailRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceDetailService {

  private final PieceDetailRepository pieceDetailRepository;
  private final PieceRepository pieceRepository;
  private final PieceDetailMapper pieceDetailMapper;

  @Transactional
  public PieceDetailResponse createPieceDetails(Long pieceId, String detailImageUrl) {
    Piece piece = pieceRepository.findById(pieceId).orElseThrow();

    PieceDetail pieceDetail = PieceDetail.builder().piece(piece).imageUrl(detailImageUrl).build();
    pieceDetailRepository.save(pieceDetail);

    return pieceDetailMapper.toPieceDetailResponse(pieceDetail);
  }
}
