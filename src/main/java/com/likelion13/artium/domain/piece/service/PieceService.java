/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.service;

import com.likelion13.artium.domain.piece.dto.reqeust.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.reqeust.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.mapper.PieceMapper;
import com.likelion13.artium.domain.pieceDetail.service.PieceDetailService;
import java.util.List;
import org.springframework.stereotype.Service;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.mapper.PieceSummaryMapper;
import com.likelion13.artium.domain.piece.repository.PieceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceService {

  private final PieceRepository pieceRepository;
  private final PieceSummaryMapper pieceSummaryMapper;
  private final PieceDetailService pieceDetailService;
  private final PieceMapper pieceMapper;

  public List<PieceSummaryResponse> getAllPieces(Long userId) {
    List<Piece> pieceList = pieceRepository.findAllByUser_IdOrderByCreatedAtDesc((userId));
    return pieceList.stream().map(pieceSummaryMapper::toPieceSummaryResponse).toList();
  }

  @Transactional
  public String createPiece(Long userId, CreatePieceRequest createPieceRequest, MultipartFile image, List<MultipartFile> pieceDetails) {
    //userId와 createPieceRequest, image를 기반으로 작품 등록

    //pieceDetails를 기반으로 디테일 컷 등록 (pieceDetailService 사용)
    return "테스트 성공";
  }

  public PieceResponse getPiece(Long pieceId) {
    Piece piece = pieceRepository.findById(pieceId).orElse(null);
    return pieceMapper.toPieceResponse(piece);
  }

  @Transactional
  public PieceResponse updatePiece(Long userId, Long pieceId, UpdatePieceRequest updatePieceRequest) {
    Piece piece = pieceRepository.findById(pieceId).orElse(null);

    //이미지 변경, 디테일 컷 변경도 추가

    piece.update(updatePieceRequest.getTitle(), updatePieceRequest.getDescription(), updatePieceRequest.getIsPurchasable());
    return pieceMapper.toPieceResponse(piece);
  }

}
