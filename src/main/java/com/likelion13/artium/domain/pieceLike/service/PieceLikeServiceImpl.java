/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.pieceLike.dto.response.PieceLikeResponse;
import com.likelion13.artium.domain.pieceLike.entity.PieceLike;
import com.likelion13.artium.domain.pieceLike.exception.PieceLikeErrorCode;
import com.likelion13.artium.domain.pieceLike.mapper.PieceLikeMapper;
import com.likelion13.artium.domain.pieceLike.repository.PieceLikeRepository;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.domain.user.service.UserService;
import com.likelion13.artium.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceLikeServiceImpl implements PieceLikeService {

  private final PieceRepository pieceRepository;
  private final UserRepository userRepository;
  private final PieceLikeMapper pieceLikeMapper;
  private final PieceLikeRepository pieceLikeRepository;
  private final UserService userService;

  @Override
  @Transactional
  public PieceLikeResponse likePiece(Long pieceId) {

    User user = userService.getCurrentUser();

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));
    try {
      PieceLike pieceLike = PieceLike.builder().piece(piece).user(user).build();

      pieceLikeRepository.save(pieceLike);
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage() != null && e.getMessage().contains("uq_piece_like_piece_user")) {
        throw new CustomException(PieceLikeErrorCode.ALREADY_LIKED);
      }
      throw e;
    }

    return pieceLikeMapper.toPieceLikeResponse(pieceId, true);
  }

  @Override
  @Transactional
  public PieceLikeResponse unlikePiece(Long pieceId) {

    User user = userService.getCurrentUser();

    pieceRepository
        .findById(pieceId)
        .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    PieceLike pieceLike =
        pieceLikeRepository
            .findByUser_IdAndPiece_Id(user.getId(), pieceId)
            .orElseThrow(() -> new CustomException(PieceLikeErrorCode.NOT_LIKED));
    pieceLikeRepository.delete(pieceLike);

    return pieceLikeMapper.toPieceLikeResponse(pieceId, false);
  }
}
