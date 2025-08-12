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
import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
import com.likelion13.artium.domain.exhibition.exception.ExhibitionErrorCode;
import com.likelion13.artium.domain.exhibition.mapper.ExhibitionMapper;
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
  private final PageMapper pageMapper;

  @Override
  @Transactional
  public String createExhibition(MultipartFile image, ExhibitionRequest request) {

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

    Exhibition exhibition = exhibitionMapper.toExhibition(imageUrl, request, status);

    try {
      exhibitionRepository.save(exhibition);
    } catch (Exception e) {
      s3Service.deleteFile(s3Service.extractKeyNameFromUrl(imageUrl));
      throw new CustomException(ExhibitionErrorCode.EXHIBITION_API_ERROR);
    }
    log.info("전시 정보 생성 성공 - id:{}, imageUrl:{}, status:{}", exhibition.getId(), imageUrl, status);

    return exhibition.getId().toString() + "번 식별자의 전시가 성공적으로 생성되었습니다.";
  }

  @Override
  public PageResponse<ExhibitionResponse> getExhibitionPage(Pageable pageable) {
    User user = userService.getCurrentUser();

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "startDate"));

    Page<ExhibitionResponse> page =
        exhibitionRepository
            .findByUserId(user.getId(), sortedPageable)
            .map(exhibitionMapper::toExhibitionResponse);

    log.info("{} 사용자의 전시 리스트 페이지 조회 - 호출된 페이지: {}", user.getNickname(), pageable.getPageNumber());
    return pageMapper.toExhibitionPageResponse(page);
  }
}
