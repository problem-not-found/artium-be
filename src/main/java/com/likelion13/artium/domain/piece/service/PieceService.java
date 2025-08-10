/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.Status;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.mapper.PieceMapper;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceService {

  private final PieceRepository pieceRepository;
  private final PieceMapper pieceMapper;
  private final UserRepository userRepository;

  public List<PieceSummaryResponse> getAllPieces(Long userId) {
    List<Piece> pieceList = pieceRepository.findAllByUser_IdOrderByCreatedAtDesc((userId));
    return pieceList.stream().map(pieceMapper::toPieceSummaryResponse).toList();
  }

  @Transactional
  public PieceResponse createPiece(
      Long userId, CreatePieceRequest createPieceRequest, String mainImageUrl) {

    User user = userRepository.findById(userId).orElseThrow();

    Piece piece =
        Piece.builder()
            .title(createPieceRequest.getTitle())
            .description(createPieceRequest.getDescription())
            .isPurchasable(createPieceRequest.getIsPurchasable())
            .status(createPieceRequest.getStatus())
            .imageUrl(mainImageUrl)
            .user(user)
            .build();

    pieceRepository.save(piece);

    return pieceMapper.toPieceResponse(piece);
  }

  public PieceResponse getPiece(Long userId, Long pieceId) {

    User user = userRepository.findById(userId).orElseThrow();
    Piece piece = pieceRepository.findById(pieceId).orElse(null);
    if (piece.getUser().getId() != user.getId()
        && (piece.getStatus() != Status.REGISTERED && piece.getStatus() != Status.ON_DISPLAY)) {
      throw new CustomException(PieceErrorCode.UNAUTHORIZED);
    }
    return pieceMapper.toPieceResponse(piece);
  }

  @Transactional
  public PieceResponse updatePiece(
      Long userId, Long pieceId, UpdatePieceRequest updatePieceRequest) {
    Piece piece = pieceRepository.findById(pieceId).orElse(null);

    // 이미지 변경, 디테일 컷 변경도 추가

    piece.update(
        updatePieceRequest.getTitle(),
        updatePieceRequest.getDescription(),
        updatePieceRequest.getIsPurchasable());
    return pieceMapper.toPieceResponse(piece);
  }
}
