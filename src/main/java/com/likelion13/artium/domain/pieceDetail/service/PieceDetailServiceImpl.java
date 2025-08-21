/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceDetail.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import com.likelion13.artium.domain.pieceDetail.repository.PieceDetailRepository;
import com.likelion13.artium.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceDetailServiceImpl implements PieceDetailService {

  private final PieceDetailRepository pieceDetailRepository;
  private final UserService userService;

  @Override
  @Transactional
  public PieceDetail createPieceDetail(Piece piece, String detailImageUrl) {

    PieceDetail pieceDetail = PieceDetail.builder().piece(piece).imageUrl(detailImageUrl).build();
    pieceDetailRepository.save(pieceDetail);

    log.info("디테일 컷 삭제 성공 - pieceId: {}, pieceDetailId: {}", piece.getId(), pieceDetail.getId());
    return pieceDetail;
  }
}
