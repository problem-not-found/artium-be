/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceDetail.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import com.likelion13.artium.domain.pieceDetail.repository.PieceDetailRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceDetailServiceImpl implements PieceDetailService {

  private final PieceDetailRepository pieceDetailRepository;

  @Override
  @Transactional
  public PieceDetail createPieceDetail(Piece piece, String detailImageUrl) {

    PieceDetail pieceDetail = PieceDetail.builder().piece(piece).imageUrl(detailImageUrl).build();
    pieceDetailRepository.save(pieceDetail);

    return pieceDetail;
  }
}
