/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.dto.request.ExhibitionRequest;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionLikeResponse;
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.domain.exhibition.exception.ExhibitionErrorCode;
import com.likelion13.artium.domain.exhibition.mapper.ExhibitionMapper;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionLike;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionPiece;
import com.likelion13.artium.domain.exhibition.repository.ExhibitionLikeRepository;
import com.likelion13.artium.domain.exhibition.repository.ExhibitionPieceRepository;
import com.likelion13.artium.domain.exhibition.repository.ExhibitionRepository;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.user.entity.User;
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
public class ExhibitionServiceImpl implements ExhibitionService {

  private final ExhibitionRepository exhibitionRepository;
  private final ExhibitionLikeRepository exhibitionLikeRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final S3Service s3Service;
  private final ExhibitionMapper exhibitionMapper;
  private final PageMapper pageMapper;
  private final ExhibitionPieceRepository exhibitionPieceRepository;
  private final PieceRepository pieceRepository;

  @Override
  @Transactional
  public ExhibitionDetailResponse createExhibition(MultipartFile image, ExhibitionRequest request) {

    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new CustomException(ExhibitionErrorCode.INVALID_DATE_RANGE);
    }

    String imageUrl = null;
    ExhibitionStatus status = determineStatus(request.getStartDate(), request.getEndDate());

    if (image != null) {
      imageUrl = s3Service.uploadFile(PathName.EXHIBITION, image);
    }

    List<ExhibitionParticipant> participants = buildParticipants(request.getParticipantIdList());
    List<ExhibitionPiece> pieces = buildPieces(request.getPieceIdList());

    User currentUser = userService.getCurrentUser();

    Exhibition exhibition =
        exhibitionMapper.toExhibition(imageUrl, request, status, currentUser, pieces, participants);

    participants.forEach(p -> p.setExhibition(exhibition));
    pieces.forEach(piece -> piece.setExhibition(exhibition));

    try {
      exhibitionRepository.save(exhibition);
    } catch (Exception e) {
      if (imageUrl != null) {
        s3Service.deleteFile(s3Service.extractKeyNameFromUrl(imageUrl));
      }
      throw new CustomException(ExhibitionErrorCode.EXHIBITION_API_ERROR);
    }

    List<Long> pieceIdList =
        exhibition.getExhibitionPieces().stream()
            .map(exhibitionPiece -> exhibitionPiece.getPiece().getId())
            .toList();
    List<Long> participantIdList =
        exhibition.getExhibitionParticipants().stream().map(p -> p.getUser().getId()).toList();

    log.info(
        "전시 정보 생성 성공 - id:{}, username:{}, status:{}",
        exhibition.getId(),
        exhibition.getUser().getUsername(),
        status);
    return exhibitionMapper.toExhibitionDetailResponse(
        exhibition, true, false, pieceIdList, participantIdList);
  }

  @Override
  @Transactional
  public ExhibitionLikeResponse createExhibitionLike(Long id) {

    Exhibition exhibition =
        exhibitionRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    User currentUser = userService.getCurrentUser();

    if (exhibition.getUser().getId().equals(currentUser.getId())) {
      throw new CustomException(ExhibitionErrorCode.CANNOT_LIKE_SELF);
    }

    try {
      ExhibitionLike exhibitionLike = exhibitionMapper.toExhibitionLike(exhibition, currentUser);

      currentUser.getExhibitionLikes().add(exhibitionLike);
      exhibitionLikeRepository.save(exhibitionLike);

      log.info(
          "새로운 전시 좋아요 생성 - 좋아요를 보낸 사용자: {}, 좋아요를 받은 전시: {}",
          exhibitionLike.getUser().getNickname(),
          exhibitionLike.getExhibition().getId());
      return exhibitionMapper.toExhibitionLikeResponse(exhibitionLike);
    } catch (DataIntegrityViolationException e) {
      throw new CustomException(UserErrorCode.ALREADY_LIKED);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ExhibitionDetailResponse getExhibition(Long id) {

    User currentUser = userService.getCurrentUser();

    Exhibition exhibition =
        exhibitionRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    if (!exhibition.getFillAll() && !exhibition.getUser().getId().equals(currentUser.getId())) {
      throw new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FILLALL);
    }

    boolean likedByCurrentUser =
        exhibitionLikeRepository.findByExhibitionAndUser(exhibition, currentUser).isPresent();
    boolean createdByCurrentUser = exhibition.getUser().getId().equals(currentUser.getId());

    List<Long> pieceIdList =
        exhibition.getExhibitionPieces().stream()
            .map(exhibitionPiece -> exhibitionPiece.getPiece().getId())
            .toList();
    List<Long> participantIdList =
        exhibition.getExhibitionParticipants().stream().map(p -> p.getUser().getId()).toList();

    return exhibitionMapper.toExhibitionDetailResponse(
        exhibition, createdByCurrentUser, likedByCurrentUser, pieceIdList, participantIdList);
  }

  @Override
  @Transactional(readOnly = true)
  public Integer getExhibitionDraftCount() {

    return exhibitionRepository
        .findByUserIdAndFillAll(userService.getCurrentUser().getId(), false)
        .size();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ExhibitionResponse> getExhibitionPageByType(
      SortBy sortBy, Pageable pageable) {
    Page<ExhibitionResponse> page;

    switch (sortBy) {
      case HOTTEST:
        page =
            exhibitionRepository
                .findAllOrderByLikesCountDesc(pageable)
                .map(exhibitionMapper::toExhibitionResponse);
        log.info("인기순 전시 리스트 페이지 조회 성공");
        break;

      case LATEST:
        LocalDate cutoffDate = LocalDate.now().minusDays(7);
        page =
            exhibitionRepository
                .findRecentOngoingExhibitions(cutoffDate, ExhibitionStatus.ONGOING, pageable)
                .map(exhibitionMapper::toExhibitionResponse);
        log.info("최신순 전시 리스트 페이지 조회 성공");
        break;

      default:
        throw new CustomException(ExhibitionErrorCode.INVALID_SORT_TYPE);
    }

    return pageMapper.toExhibitionPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ExhibitionResponse> getExhibitionPageByUser(
      Boolean fillAll, Pageable pageable) {
    User user = userService.getCurrentUser();

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "startDate"));

    Page<ExhibitionResponse> page =
        exhibitionRepository
            .findByUserIdAndFillAll(user.getId(), fillAll, sortedPageable)
            .map(exhibitionMapper::toExhibitionResponse);

    log.info(
        "{} 사용자의 전시 리스트 페이지 조회 - 호출된 페이지: {}, 등록 완료 여부: {}",
        user.getNickname(),
        pageable.getPageNumber(),
        fillAll);
    return pageMapper.toExhibitionPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ExhibitionResponse> getExhibitionPageByUserId(
      Long userId, Pageable pageable) {

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "startDate"));

    Page<ExhibitionResponse> page =
        exhibitionRepository
            .findByUserIdAndFillAll(user.getId(), true, sortedPageable)
            .map(exhibitionMapper::toExhibitionResponse);

    log.info("{} 사용자의 전시 리스트 페이지 조회 - 호출된 페이지: {}", user.getNickname(), pageable.getPageNumber());
    return pageMapper.toExhibitionPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ExhibitionResponse> getExhibitionPageByLike(Pageable pageable) {

    User user = userService.getCurrentUser();

    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

    Page<ExhibitionResponse> page =
        exhibitionRepository
            .findLikedExhibitionsByUserId(user.getId(), sortedPageable)
            .map(exhibitionMapper::toExhibitionResponse);

    log.info(
        "{} 사용자가 좋아요 한 전시 리스트 페이지 조회 - 호출된 페이지: {}", user.getNickname(), pageable.getPageNumber());
    return pageMapper.toExhibitionPageResponse(page);
  }

  @Override
  @Transactional
  public ExhibitionDetailResponse updateExhibition(
      Long id, MultipartFile image, ExhibitionRequest request) {

    Exhibition exhibition =
        exhibitionRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    User currentUser = userService.getCurrentUser();
    if (!exhibition.getUser().equals(currentUser)) {
      throw new CustomException(ExhibitionErrorCode.EXHIBITION_ACCESS_DENIED);
    }

    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new CustomException(ExhibitionErrorCode.INVALID_DATE_RANGE);
    }

    String imageUrl;

    if (image != null) {
      String newImageUrl = s3Service.uploadFile(PathName.EXHIBITION, image);

      if (exhibition.getThumbnailImageUrl() != null) {
        s3Service.deleteFile(s3Service.extractKeyNameFromUrl(exhibition.getThumbnailImageUrl()));
      }
      imageUrl = newImageUrl;
    } else {
      imageUrl = exhibition.getThumbnailImageUrl();
    }

    try {
      ExhibitionStatus status = determineStatus(request.getStartDate(), request.getEndDate());

      List<ExhibitionParticipant> participants = buildParticipants(request.getParticipantIdList());
      List<ExhibitionPiece> pieces = buildPieces(request.getPieceIdList());

      exhibition.getExhibitionParticipants().clear();
      participants.forEach(p -> p.setExhibition(exhibition));
      exhibition.getExhibitionParticipants().addAll(participants);

      exhibition.getExhibitionPieces().clear();
      pieces.forEach(p -> p.setExhibition(exhibition));
      exhibition.getExhibitionPieces().addAll(pieces);

      Exhibition updatedExhibition =
          exhibitionMapper.toExhibition(
              imageUrl, request, status, currentUser, pieces, participants);

      exhibition.update(updatedExhibition);
    } catch (Exception e) {
      log.error("오류 로그: ", e);
      throw new CustomException(ExhibitionErrorCode.EXHIBITION_API_ERROR);
    }

    List<Long> pieceIdList =
        exhibition.getExhibitionPieces().stream()
            .map(exhibitionPiece -> exhibitionPiece.getPiece().getId())
            .toList();
    List<Long> participantIdList =
        exhibition.getExhibitionParticipants().stream().map(p -> p.getUser().getId()).toList();

    log.info(
        "전시 정보 수정 성공 - id: {}, status: {}", exhibition.getId(), exhibition.getExhibitionStatus());
    return exhibitionMapper.toExhibitionDetailResponse(
        exhibition, true, false, pieceIdList, participantIdList);
  }

  @Override
  @Transactional
  public ExhibitionLikeResponse deleteExhibitionLike(Long id) {

    Exhibition exhibition =
        exhibitionRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    User currentUser = userService.getCurrentUser();

    if (exhibition.getUser().getId().equals(currentUser.getId())) {
      throw new CustomException(ExhibitionErrorCode.CANNOT_LIKE_SELF);
    }

    ExhibitionLike exhibitionLike =
        exhibitionLikeRepository
            .findByExhibitionAndUser(exhibition, currentUser)
            .orElseThrow(() -> new CustomException(UserErrorCode.LIKE_NOT_FOUND));

    exhibitionLikeRepository.delete(exhibitionLike);

    log.info(
        "전시 좋아요 삭제 - 좋아요를 취소한 사용자: {}, 전시 ID: {}", currentUser.getNickname(), exhibition.getId());

    return exhibitionMapper.toExhibitionLikeResponse(exhibitionLike);
  }

  private ExhibitionStatus determineStatus(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      throw new CustomException(ExhibitionErrorCode.INVALID_DATE_RANGE);
    }
    LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

    if (endDate.isBefore(now) || endDate.isEqual(now)) {
      return ExhibitionStatus.ENDED;
    } else if (startDate.isBefore(now) || startDate.isEqual(now)) {
      return ExhibitionStatus.ONGOING;
    } else {
      return ExhibitionStatus.UPCOMING;
    }
  }

  private List<ExhibitionParticipant> buildParticipants(List<Long> userIds) {
    return userIds.stream()
        .distinct()
        .map(
            userId -> {
              User participantUser =
                  userRepository
                      .findById(userId)
                      .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
              return ExhibitionParticipant.builder().exhibition(null).user(participantUser).build();
            })
        .collect(Collectors.toList());
  }

  private List<ExhibitionPiece> buildPieces(List<Long> pieceIds) {
    return pieceIds.stream()
        .distinct()
        .map(
            pieceId -> {
              Piece exhbitionPiece =
                  pieceRepository
                      .findById(pieceId)
                      .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));
              return ExhibitionPiece.builder().exhibition(null).piece(exhbitionPiece).build();
            })
        .collect(Collectors.toList());
  }
}
