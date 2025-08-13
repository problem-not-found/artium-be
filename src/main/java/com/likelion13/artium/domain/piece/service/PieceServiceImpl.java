/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.mapper.PieceMapper;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import com.likelion13.artium.domain.pieceDetail.service.PieceDetailService;
import com.likelion13.artium.domain.pieceLike.repository.PieceLikeRepository;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.domain.user.service.UserService;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.mapper.PageMapper;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.s3.entity.PathName;
import com.likelion13.artium.global.s3.service.S3Service;

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
  private final PieceLikeRepository pieceLikeRepository;
  private final UserService userService;
  private final PageMapper pageMapper;
  private final UserRepository userRepository;

  @Override
  public PageResponse<PieceSummaryResponse> getPiecePage(Long userId, Pageable pageable) {
    userRepository
        .findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "createdAt"));

    List<ProgressStatus> statuses = List.of(ProgressStatus.REGISTERED, ProgressStatus.ON_DISPLAY);

    Page<PieceSummaryResponse> page =
        pieceRepository
            .findByUserIdAndProgressStatusIn(userId, statuses, sortedPageable)
            .map(pieceMapper::toPieceSummaryResponse);

    return pageMapper.toPiecePageResponse(page);
  }

  @Override
  public PageResponse<PieceSummaryResponse> getMyPiecePage(Boolean applicated, Pageable pageable) {
    Long userId = userService.getCurrentUser().getId();

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "createdAt"));

    Page<PieceSummaryResponse> page =
        applicated
            ? pieceRepository
                .findByUserIdAndSaveStatusNot(userId, SaveStatus.DRAFT, sortedPageable)
                .map(pieceMapper::toPieceSummaryResponse)
            : pieceRepository
                .findByUserIdAndSaveStatus(userId, SaveStatus.DRAFT, sortedPageable)
                .map(pieceMapper::toPieceSummaryResponse);

    return pageMapper.toPiecePageResponse(page);
  }

  @Override
  @Transactional
  public PieceSummaryResponse createPiece(
      CreatePieceRequest createPieceRequest,
      SaveStatus saveStatus,
      MultipartFile mainImage,
      List<MultipartFile> detailImages) {

    boolean isValidatedPiece = validateCreatePieceFields(createPieceRequest, mainImage);

    if (saveStatus == SaveStatus.APPLICATION && !isValidatedPiece) {
      throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
    }

    String mainImageUrl =
        mainImage != null ? s3Service.uploadFile(PathName.PIECE, mainImage) : null;

    Piece piece =
        Piece.builder()
            .title(createPieceRequest.getTitle())
            .description(createPieceRequest.getDescription())
            .isPurchasable(createPieceRequest.getIsPurchasable())
            .saveStatus(saveStatus)
            .imageUrl(mainImageUrl)
            .user(userService.getCurrentUser())
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
  public PieceResponse getPiece(Long pieceId) {
    Long userId = userService.getCurrentUser().getId();
    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));
    if (!piece.getUser().getId().equals(userId)
        && (piece.getProgressStatus() != ProgressStatus.REGISTERED
            && piece.getProgressStatus() != ProgressStatus.ON_DISPLAY)) {
      throw new CustomException(PieceErrorCode.UNAUTHORIZED);
    }
    if (pieceLikeRepository.findByUser_IdAndPiece_Id(userId, pieceId).isPresent()) {
      return pieceMapper.toPieceResponseWithLike(piece, true);
    } else {
      return pieceMapper.toPieceResponseWithLike(piece, false);
    }
  }

  @Override
  @Transactional
  public PieceResponse updatePiece(
      Long pieceId,
      UpdatePieceRequest updatePieceRequest,
      SaveStatus saveStatus,
      MultipartFile mainImage,
      List<MultipartFile> detailImages) {

    Long userId = userService.getCurrentUser().getId();

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    if (!piece.getUser().getId().equals(userId)) {
      throw new CustomException(PieceErrorCode.UNAUTHORIZED);
    }

    boolean isValidatedPiece;
    if (piece.getImageUrl() == null) {
      isValidatedPiece = validateUpdatePieceFields(updatePieceRequest, mainImage);
    } else {
      isValidatedPiece = validateUpdatePieceFields(updatePieceRequest);
    }

    if (saveStatus == SaveStatus.APPLICATION && !isValidatedPiece) {
      throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
    }

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
        saveStatus);
    if (pieceLikeRepository.findByUser_IdAndPiece_Id(userId, pieceId).isPresent()) {
      return pieceMapper.toPieceResponseWithLike(piece, true);
    } else {
      return pieceMapper.toPieceResponseWithLike(piece, false);
    }
  }

  @Override
  @Transactional
  public void deletePiece(Long pieceId) {
    Long userId = userService.getCurrentUser().getId();

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    if (!piece.getUser().getId().equals(userId)) {
      throw new CustomException(PieceErrorCode.UNAUTHORIZED);
    }

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

  @Override
  public Integer getPieceDraftCount() {

    return pieceRepository.countByUserIdAndSaveStatus(
        userService.getCurrentUser().getId(), SaveStatus.DRAFT);
  }

  private boolean validateCreatePieceFields(
      CreatePieceRequest createPieceRequest, MultipartFile mainImage) {
    return createPieceRequest.getTitle() != null
        && createPieceRequest.getDescription() != null
        && createPieceRequest.getIsPurchasable() != null
        && mainImage != null;
  }

  private boolean validateUpdatePieceFields(
      UpdatePieceRequest updatePieceRequest, MultipartFile mainImage) {
    return updatePieceRequest.getTitle() != null
        && updatePieceRequest.getDescription() != null
        && updatePieceRequest.getIsPurchasable() != null
        && mainImage != null;
  }

  private boolean validateUpdatePieceFields(UpdatePieceRequest updatePieceRequest) {
    return updatePieceRequest.getTitle() != null
        && updatePieceRequest.getDescription() != null
        && updatePieceRequest.getIsPurchasable() != null;
  }
}
