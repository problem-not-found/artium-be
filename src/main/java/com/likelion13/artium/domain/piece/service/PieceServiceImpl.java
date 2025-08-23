/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.domain.piece.dto.request.CreatePieceRequest;
import com.likelion13.artium.domain.piece.dto.request.UpdatePieceRequest;
import com.likelion13.artium.domain.piece.dto.response.PieceFeedResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.dto.response.PieceSummaryResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.piece.entity.RecommendSortBy;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.mapper.PieceMapper;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import com.likelion13.artium.domain.pieceDetail.service.PieceDetailService;
import com.likelion13.artium.domain.pieceLike.repository.PieceLikeRepository;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.domain.user.service.UserService;
import com.likelion13.artium.global.ai.embedding.service.EmbeddingService;
import com.likelion13.artium.global.ai.vector.VectorUtils;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.mapper.PageMapper;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.qdrant.entity.CollectionName;
import com.likelion13.artium.global.qdrant.service.QdrantService;
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
  private final QdrantService qdrantService;
  private final EmbeddingService embeddingService;

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

    log.info(
        "특정 사용자 작품 리스트 조회 성공 - requestUserId: {}, piece.userId: {}",
        userService.getCurrentUser().getId(),
        userId);
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

    log.info("내 작품 리스트 조회 성공 - requestUserId: {}", userId);
    return pageMapper.toPiecePageResponse(page);
  }

  @Override
  @Transactional
  public PieceSummaryResponse createPiece(
      CreatePieceRequest createPieceRequest,
      SaveStatus saveStatus,
      MultipartFile mainImage,
      List<MultipartFile> detailImages) {
    User user = userService.getCurrentUser();

    String mainImageUrl =
        mainImage != null ? s3Service.uploadFile(PathName.PIECE, mainImage) : null;
    Piece piece =
        Piece.builder()
            .title(createPieceRequest.getTitle())
            .description(createPieceRequest.getDescription())
            .isPurchasable(createPieceRequest.getIsPurchasable())
            .saveStatus(saveStatus)
            .imageUrl(mainImageUrl)
            .user(user)
            .build();

    pieceRepository.save(piece);

    if (saveStatus == SaveStatus.APPLICATION) {
      piece.updateProgressStatus(ProgressStatus.REGISTERED);
      String content = (piece.getTitle() + "\n\n" + piece.getDescription()).trim();
      float[] vector = embeddingService.embed(content);

      qdrantService.upsertPiecePoint(piece.getId(), vector, piece, CollectionName.PIECE);
    }

    if (detailImages != null && !detailImages.isEmpty()) {
      List<String> detailImageUrls =
          detailImages.stream()
              .map(detailImage -> s3Service.uploadFile(PathName.PIECE_DETAIL, detailImage))
              .toList();
      detailImageUrls.forEach(
          detailImageUrl -> pieceDetailService.createPieceDetail(piece, detailImageUrl));
    }

    log.info(
        "작품 등록 성공 - userId: {}, pieceId: {}, saveStatus: {}",
        user.getId(),
        piece.getId(),
        saveStatus);
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
      throw new CustomException(PieceErrorCode.FORBIDDEN);
    }

    log.info("특정 작품 조회 성공 - userId: {}, pieceId: {}", userId, pieceId);
    return pieceMapper.toPieceResponseWithLike(
        piece, pieceLikeRepository.existsByUser_IdAndPiece_Id(userId, pieceId));
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
      log.error(
          "작품에 접근 권한이 없습니다 - requestUserId: {}, piece.userId: {}", userId, piece.getUser().getId());
      throw new CustomException(PieceErrorCode.FORBIDDEN);
    }

    if (saveStatus == SaveStatus.APPLICATION) {
      if (piece.getImageUrl() == null
          && !validateUpdatePieceFields(updatePieceRequest, mainImage)) {
        log.error("등록 신청을 하려면 제목, 설명, 구매 가능 여부, 메인 이미지를 입력해야 합니다.");
        throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
      } else if (piece.getImageUrl() != null && !validateUpdatePieceFields(updatePieceRequest)) {
        log.error("등록 신청을 하려면 제목, 설명, 구매 가능 여부, 메인 이미지를 입력해야 합니다.");
        throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
      }
    }

    List<Long> pieceDetailIds = piece.getPieceDetails().stream().map(PieceDetail::getId).toList();
    updatePieceRequest
        .getRemainPieceDetailIds()
        .forEach(
            remainPieceDetailId -> {
              if (!pieceDetailIds.contains(remainPieceDetailId)) {
                log.error(
                    "해당 작품의 디테일 컷이 아닙니다. - remainPieceDetailId: {}, pieceDetailIds: {}",
                    remainPieceDetailId,
                    piece.getPieceDetails().stream().map(PieceDetail::getId).toList());
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
    log.info("작품 수정에 성공했습니다. - userId: {}, pieceId: {}", userId, pieceId);
    return pieceMapper.toPieceResponse(piece);
  }

  @Override
  @Transactional
  public String deletePieces(List<Long> pieceIds) {
    Long userId = userService.getCurrentUser().getId();

    List<Piece> pieces = pieceRepository.findAllById(pieceIds);

    pieces.forEach(
        piece -> {
          if (!piece.getUser().getId().equals(userId)) {
            log.error("작품에 접근 권한이 없습니다 - requestUserId: {}, pieceId: {}", userId, piece.getId());
            throw new CustomException(PieceErrorCode.FORBIDDEN);
          }
          if (piece.getImageUrl() != null) {
            s3Service.deleteFile(s3Service.extractKeyNameFromUrl(piece.getImageUrl()));
          }
          piece
              .getPieceDetails()
              .forEach(
                  pieceDetail -> {
                    s3Service.deleteFile(
                        s3Service.extractKeyNameFromUrl(pieceDetail.getImageUrl()));
                  });

          pieceRepository.delete(piece);
        });

    log.info("작품 리스트 삭제에 성공했습니다. userId: {}, pieceIds: {}", userId, pieceIds);
    return "작품 리스트 삭제에 성공했습니다.";
  }

  @Override
  public Integer getPieceDraftCount() {

    return pieceRepository.countByUserIdAndSaveStatus(
        userService.getCurrentUser().getId(), SaveStatus.DRAFT);
  }

  @Override
  public PageResponse<PieceSummaryResponse> getLikePieces(Pageable pageable) {
    Long userId = userService.getCurrentUser().getId();

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "createdAt"));

    Page<PieceSummaryResponse> page =
        pieceLikeRepository
            .findPieceByUser_Id(userId, sortedPageable)
            .map(pieceMapper::toPieceSummaryResponse);

    log.info("좋아요 한 작품 리스트 조회에 성공했습니다. - userId: {}", userId);
    return pageMapper.toPiecePageResponse(page);
  }

  @Override
  public PageResponse<PieceFeedResponse> getRecommendationPiecePage(
      RecommendSortBy sortBy, Pageable pageable) {
    Long userId = userService.getCurrentUser().getId();

    List<Long> recommendPieceIds = recommendPieceIds(userId, pageable.getPageSize());
    Page<PieceFeedResponse> page;

    log.info("추천 작품 아이디 리스트 - recommendPieceIds : {} ", recommendPieceIds);
    switch (sortBy) {
      case FAVORITE:
        page =
            pieceRepository
                .findByIdIn(recommendPieceIds, pageable)
                .map(
                    piece ->
                        pieceMapper.toPieceFeedResponse(
                            piece,
                            pieceLikeRepository.existsByUser_IdAndPiece_Id(
                                userId, piece.getUser().getId())));
        log.info("관심사 기반 취향 저격 작품 리스트 조회 성공");
        break;
      case NEW_STYLE:
        recommendPieceIds = new ArrayList<>(recommendPieceIds);
        List<Long> myPieceIds =
            pieceRepository.findByUser_Id(userId).stream().map(Piece::getId).toList();
        if (!myPieceIds.isEmpty()) {
          recommendPieceIds.addAll(myPieceIds);
        }
        List<ProgressStatus> statuses =
            new ArrayList<>(Arrays.asList(ProgressStatus.REGISTERED, ProgressStatus.ON_DISPLAY));
        if (recommendPieceIds.isEmpty()) {
          page =
              pieceRepository
                  .findByProgressStatusIn(statuses, pageable)
                  .map(
                      piece ->
                          pieceMapper.toPieceFeedResponse(
                              piece,
                              pieceLikeRepository.existsByUser_IdAndPiece_Id(
                                  userId, piece.getUser().getId())));
        } else {
          page =
              pieceRepository
                  .findByIdNotInAndProgressStatusInNolikes(recommendPieceIds, statuses, pageable)
                  .map(
                      piece ->
                          pieceMapper.toPieceFeedResponse(
                              piece,
                              pieceLikeRepository.existsByUser_IdAndPiece_Id(
                                  userId, piece.getUser().getId())));
        }
        log.info("색다른 도전 작품 리스트 조회 성공");
        break;
      default:
        throw new CustomException(PieceErrorCode.INVALID_SORT_TYPE);
    }

    return pageMapper.toPieceFeedPageResponse(page);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PieceSummaryResponse> getPieceListByKeyword(String keyword, SortBy sortBy) {
    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }

    List<Piece> pieces = pieceRepository.findAll();

    List<Piece> filtered =
        pieces.stream()
            .filter(
                p ->
                    p.getProgressStatus() == ProgressStatus.REGISTERED
                        || p.getProgressStatus() == ProgressStatus.ON_DISPLAY)
            .filter(
                p ->
                    (p.getTitle() != null && p.getTitle().contains(keyword))
                        || (p.getDescription() != null && p.getDescription().contains(keyword)))
            .toList();

    List<Piece> sorted;
    if (sortBy == SortBy.HOTTEST) {
      sorted =
          filtered.stream()
              .sorted(Comparator.comparingInt((Piece p) -> p.getPieceLikes().size()).reversed())
              .toList();
    } else if (sortBy == SortBy.LATEST) {
      sorted =
          filtered.stream().sorted(Comparator.comparing(Piece::getCreatedAt).reversed()).toList();
    } else {
      sorted = filtered;
    }

    log.info("키워드를 통한 작품 검색 성공 - 키워드: {}, 정렬: {}", keyword, sortBy);
    return sorted.stream().map(pieceMapper::toPieceSummaryResponse).toList();
  }

  @Override
  public PageResponse<PieceFeedResponse> getPiecePageByType(SortBy sortBy, Pageable pageable) {
    Long userId = userService.getCurrentUser().getId();
    Page<PieceFeedResponse> page;

    switch (sortBy) {
      case HOTTEST:
        page =
            pieceRepository
                .findAllOrderByLikesCountDesc(pageable)
                .map(
                    piece ->
                        pieceMapper.toPieceFeedResponse(
                            piece,
                            pieceLikeRepository.existsByUser_IdAndPiece_Id(userId, piece.getId())));
        log.info("인기순 작품 리스트 페이지 조회 성공");
        break;

      case LATEST:
        page =
            pieceRepository
                .findRecentOngoingPieces(ProgressStatus.WAITING, pageable)
                .map(
                    piece ->
                        pieceMapper.toPieceFeedResponse(
                            piece,
                            pieceLikeRepository.existsByUser_IdAndPiece_Id(userId, piece.getId())));
        log.info("최신순 작품 리스트 페이지 조회 성공");
        break;

      default:
        throw new CustomException(PieceErrorCode.INVALID_SORT_TYPE);
    }

    return pageMapper.toPieceFeedPageResponse(page);
  }

  private List<Long> recommendPieceIds(Long userId, int topN) {
    int limit = (topN < 0) ? 50 : topN;

    float[] userVector =
        VectorUtils.normalize(qdrantService.retrieveVectorById(userId, CollectionName.USER));
    if (userVector == null || userVector.length == 0) {
      log.error("사용자 임베딩 벡터가 없어서 추천을 건너뜁니다. - userId: {}", userId);
      return List.of();
    }
    List<Long> excludeIds =
        pieceRepository.findByUser_Id(userId).stream().map(Piece::getId).toList();

    List<Map<String, Object>> result =
        qdrantService.search(userVector, limit, excludeIds, CollectionName.PIECE);

    List<Map<String, Object>> sorted = new ArrayList<>(result);
    sorted.sort(
        (a, b) -> {
          double sb = ((Number) b.get("score")).doubleValue();
          double sa = ((Number) a.get("score")).doubleValue();
          return Double.compare(sb, sa);
        });
    sorted = sorted.subList(0, Math.min(limit, sorted.size()));

    List<Long> candidateIds =
        sorted.stream()
            .map(m -> (Map<String, Object>) m.get("payload"))
            .filter(Objects::nonNull)
            .map(p -> ((Number) p.get("pieceId")).longValue())
            .toList();
    log.info("추천 기반 후보 작품 아이디 리스트 - candidateIds : {} ", candidateIds);
    Map<Long, Integer> pos = new HashMap<>();
    for (int i = 0; i < candidateIds.size(); i++) pos.put(candidateIds.get(i), i);

    List<Long> filtered =
        candidateIds.isEmpty()
            ? List.of()
            : pieceRepository
                .findIdsByIdsInAndProgressStatusIn(
                    candidateIds,
                    List.of(ProgressStatus.REGISTERED, ProgressStatus.ON_DISPLAY),
                    Pageable.unpaged())
                .getContent()
                .stream()
                .toList();

    List<Long> recommendIds =
        filtered.stream()
            .sorted(Comparator.comparingInt(id -> pos.getOrDefault(id, Integer.MAX_VALUE)))
            .toList();

    return recommendIds;
  }

  private boolean validateUpdatePieceFields(
      UpdatePieceRequest updatePieceRequest, MultipartFile mainImage) {
    return (updatePieceRequest.getTitle() != null && !updatePieceRequest.getTitle().isEmpty())
        && (updatePieceRequest.getDescription() != null
            && !updatePieceRequest.getDescription().isEmpty())
        && updatePieceRequest.getIsPurchasable() != null
        && (mainImage != null && !mainImage.isEmpty());
  }

  private boolean validateUpdatePieceFields(UpdatePieceRequest updatePieceRequest) {
    return (updatePieceRequest.getTitle() != null && !updatePieceRequest.getTitle().isEmpty())
        && (updatePieceRequest.getDescription() != null
            && !updatePieceRequest.getDescription().isEmpty())
        && updatePieceRequest.getIsPurchasable() != null;
  }
}
