/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.service;

import java.util.Optional;

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
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.security.CustomUserDetails;

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

  @Override
  @Transactional
  public PieceLikeResponse likePiece(CustomUserDetails userDetails, Long pieceId) {
    User user = userRepository.getReferenceById(userDetails.getUser().getId());

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));
    try {
      PieceLike pieceLike = PieceLike.builder().piece(piece).user(user).build();

      pieceLikeRepository.save(pieceLike);
    } catch (DataIntegrityViolationException e) {
      throw new CustomException(PieceLikeErrorCode.ALREADY_LIKED);
    }

    return pieceLikeMapper.toPieceLikeResponse(pieceId, true);
  }

  @Override
  @Transactional
  public PieceLikeResponse unlikePiece(CustomUserDetails userDetails, Long pieceId) {
    pieceRepository
        .findById(pieceId)
        .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    Optional<PieceLike> pieceLike =
        Optional.ofNullable(
            pieceLikeRepository
                .findByUser_IdAndPiece_Id(userDetails.getUser().getId(), pieceId)
                .orElseThrow(() -> new CustomException(PieceLikeErrorCode.NOT_LIKED)));
    pieceLike.ifPresent(pieceLikeRepository::delete);

    return pieceLikeMapper.toPieceLikeResponse(pieceId, false);
  }
}
