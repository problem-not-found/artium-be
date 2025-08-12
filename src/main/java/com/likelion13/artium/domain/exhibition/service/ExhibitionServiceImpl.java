/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.service;

import java.time.LocalDate;
import java.time.ZoneId;

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
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
import com.likelion13.artium.domain.exhibition.entity.SortBy;
import com.likelion13.artium.domain.exhibition.exception.ExhibitionErrorCode;
import com.likelion13.artium.domain.exhibition.mapper.ExhibitionMapper;
import com.likelion13.artium.domain.exhibition.mapper.ExhibitionUserMapper;
import com.likelion13.artium.domain.exhibition.repository.ExhibitionRepository;
import com.likelion13.artium.domain.user.entity.User;
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
  private final UserService userService;
  private final S3Service s3Service;
  private final ExhibitionMapper exhibitionMapper;
  private final ExhibitionUserMapper exhibitionUserMapper;
  private final PageMapper pageMapper;

  @Override
  @Transactional
  public ExhibitionDetailResponse createExhibition(MultipartFile image, ExhibitionRequest request) {

    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new CustomException(ExhibitionErrorCode.INVALID_DATE_RANGE);
    }

    String imageUrl = null;
    ExhibitionStatus status = ExhibitionStatus.UPCOMING;
    LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

    if (image != null) {
      imageUrl = s3Service.uploadFile(PathName.EXHIBITION, image);
    }

    if (request.getEndDate().isBefore(now) || request.getEndDate().isEqual(now)) {
      status = ExhibitionStatus.ENDED;
    } else if (request.getStartDate().isBefore(now) || request.getStartDate().isEqual(now)) {
      status = ExhibitionStatus.ONGOING;
    }

    Exhibition exhibition =
        exhibitionMapper.toExhibition(imageUrl, request, status, userService.getCurrentUser());

    try {
      exhibitionRepository.save(exhibition);
    } catch (Exception e) {
      s3Service.deleteFile(s3Service.extractKeyNameFromUrl(imageUrl));
      throw new CustomException(ExhibitionErrorCode.EXHIBITION_API_ERROR);
    }
    log.info(
        "전시 정보 생성 성공 - id:{}, username:{}, status:{}",
        exhibition.getId(),
        exhibition.getUser().getUsername(),
        status);

    return exhibitionMapper.toExhibitionDetailResponse(exhibition);
  }

  @Override
  public ExhibitionDetailResponse getExhibition(Long id) {

    Exhibition exhibition =
        exhibitionRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    return exhibitionMapper.toExhibitionDetailResponse(exhibition);
  }

  @Override
  public Integer getExhibitionDraftCount() {

    return exhibitionRepository
        .findByUserIdAndFillAll(userService.getCurrentUser().getId(), false)
        .size();
  }

  @Override
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
  @Transactional
  public ExhibitionDetailResponse updateExhibition(
      Long id, MultipartFile image, ExhibitionRequest request) {

    Exhibition exhibition =
        exhibitionRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

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
      LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
      ExhibitionStatus status = ExhibitionStatus.UPCOMING;

      if (request.getEndDate().isBefore(now) || request.getEndDate().isEqual(now)) {
        status = ExhibitionStatus.ENDED;
      } else if (request.getStartDate().isBefore(now) || request.getStartDate().isEqual(now)) {
        status = ExhibitionStatus.ONGOING;
      }

      Exhibition updatedExhibition =
          exhibitionMapper.toExhibition(imageUrl, request, status, userService.getCurrentUser());

      exhibition.update(updatedExhibition);
    } catch (Exception e) {
      log.error("오류 로그: ", e);
      throw new CustomException(ExhibitionErrorCode.EXHIBITION_API_ERROR);
    }

    log.info(
        "전시 정보 수정 성공 - id: {}, status: {}", exhibition.getId(), exhibition.getExhibitionStatus());
    return exhibitionMapper.toExhibitionDetailResponse(exhibition);
  }
}
