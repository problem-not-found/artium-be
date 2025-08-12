/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.Status;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.mapper.PieceMapper;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import com.likelion13.artium.domain.pieceDetail.service.PieceDetailService;
import com.likelion13.artium.domain.pieceLike.repository.PieceLikeRepository;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.s3.entity.PathName;
import com.likelion13.artium.global.s3.service.S3Service;
import com.likelion13.artium.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceServiceImpl implements PieceService {

  private final PieceRepository pieceRepository;
  private final PieceMapper pieceMapper;
  private final S3Service s3Service;
  private final PieceDetailService pieceDetailService;
  private final UserRepository userRepository;
  private final PieceLikeRepository pieceLikeRepository;

  @Override
  public List<PieceSummaryResponse> getAllPieces(CustomUserDetails userDetails) {
    List<Piece> pieceList =
        pieceRepository.findAllByUser_IdOrderByCreatedAtDesc(userDetails.getUser().getId());
    return pieceList.stream().map(pieceMapper::toPieceSummaryResponse).toList();
  }

  @Override
  @Transactional
  public PieceSummaryResponse createPiece(
      CustomUserDetails userDetails,
      CreatePieceRequest createPieceRequest,
      MultipartFile mainImage,
      List<MultipartFile> detailImages) {

    String mainImageUrl =
        mainImage != null ? s3Service.uploadFile(PathName.PIECE, mainImage) : null;
    Piece piece =
        Piece.builder()
            .title(createPieceRequest.getTitle())
            .description(createPieceRequest.getDescription())
            .isPurchasable(createPieceRequest.getIsPurchasable())
            .status(createPieceRequest.getStatus())
            .imageUrl(mainImageUrl)
            .user(userDetails.getUser())
            .build();

    pieceRepository.save(piece);

    if (detailImages != null && !detailImages.isEmpty()) {
      List<String> detailImageUrls =
          detailImages.stream()
              .map(detailImage -> s3Service.uploadFile(PathName.PIECE_DETAIL, detailImage))
              .toList();
      detailImageUrls.forEach(
          detailImageUrl -> pieceDetailService.createPieceDetail(piece, detailImageUrl));
    }

    return pieceMapper.toPieceSummaryResponse(piece);
  }

  @Override
  public PieceResponse getPiece(CustomUserDetails userDetails, Long pieceId) {

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));
    if (!piece.getUser().getId().equals(userDetails.getUser().getId())
        && (piece.getStatus() != Status.REGISTERED && piece.getStatus() != Status.ON_DISPLAY)) {
      throw new CustomException(PieceErrorCode.UNAUTHORIZED);
    }
    if (pieceLikeRepository
        .findByUser_IdAndPiece_Id(userDetails.getUser().getId(), pieceId)
        .isPresent()) {
      return pieceMapper.toPieceResponseWithLike(piece, true);
    } else {
      return pieceMapper.toPieceResponseWithLike(piece, false);
    }
  }

  @Override
  @Transactional
  public PieceResponse updatePiece(
      CustomUserDetails userDetails,
      Long pieceId,
      UpdatePieceRequest updatePieceRequest,
      MultipartFile mainImage,
      List<MultipartFile> detailImages) {
    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    List<Long> pieceDetailIds = piece.getPieceDetails().stream().map(PieceDetail::getId).toList();
    updatePieceRequest
        .getRemainPieceDetailIds()
        .forEach(
            remainPieceDetailId -> {
              if (!pieceDetailIds.contains(remainPieceDetailId)) {
                throw new CustomException(PieceErrorCode.DETAIL_IMAGE_NOT_BELONG_TO_PIECE);
              }
            });

    if (mainImage != null && !mainImage.isEmpty()) {
      String mainImageUrl = s3Service.uploadFile(PathName.PIECE, mainImage);
      String oldUrl = piece.updateImageUrl(mainImageUrl);
      if (oldUrl != null) s3Service.deleteFile(s3Service.extractKeyNameFromUrl(oldUrl));
    }

    piece
        .getPieceDetails()
        .removeIf(
            pieceDetail -> {
              if (!updatePieceRequest.getRemainPieceDetailIds().contains(pieceDetail.getId())) {
                s3Service.deleteFile(s3Service.extractKeyNameFromUrl(pieceDetail.getImageUrl()));
                return true;
              }
              return false;
            });

    if (detailImages != null && !detailImages.isEmpty()) {
      List<String> detailImageUrls =
          detailImages.stream()
              .map(detailImage -> s3Service.uploadFile(PathName.PIECE_DETAIL, detailImage))
              .toList();
      List<PieceDetail> pieceDetails =
          detailImageUrls.stream()
              .map(detailImageUrl -> pieceDetailService.createPieceDetail(piece, detailImageUrl))
              .toList();

      pieceDetails.forEach(piece::addPieceDetail);
    }

    piece.update(
        updatePieceRequest.getTitle(),
        updatePieceRequest.getDescription(),
        updatePieceRequest.getIsPurchasable(),
        updatePieceRequest.getStatus());
    if (pieceLikeRepository
        .findByUser_IdAndPiece_Id(userDetails.getUser().getId(), pieceId)
        .isPresent()) {
      return pieceMapper.toPieceResponseWithLike(piece, true);
    } else {
      return pieceMapper.toPieceResponseWithLike(piece, false);
    }
  }

  @Override
  @Transactional
  public void deletePiece(CustomUserDetails userDetails, Long pieceId) {
    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    if (piece.getImageUrl() != null) {
      s3Service.deleteFile(s3Service.extractKeyNameFromUrl(piece.getImageUrl()));
    }
    piece
        .getPieceDetails()
        .forEach(
            pieceDetail ->
                s3Service.deleteFile(s3Service.extractKeyNameFromUrl(pieceDetail.getImageUrl())));

    pieceRepository.delete(piece);
  }
}
