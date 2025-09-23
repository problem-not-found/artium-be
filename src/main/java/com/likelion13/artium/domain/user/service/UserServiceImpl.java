/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
import com.likelion13.artium.domain.exhibition.entity.ParticipateStatus;
import com.likelion13.artium.domain.exhibition.exception.ExhibitionErrorCode;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;
import com.likelion13.artium.domain.exhibition.repository.ExhibitionRepository;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.request.UpdateContactRequest;
import com.likelion13.artium.domain.user.dto.request.UpdateUserInfoRequest;
import com.likelion13.artium.domain.user.dto.request.UpdateUserRequest;
import com.likelion13.artium.domain.user.dto.response.CreatorFeedResponse;
import com.likelion13.artium.domain.user.dto.response.CreatorResponse;
import com.likelion13.artium.domain.user.dto.response.PreferenceResponse;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserContactResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.dto.response.UserLikeResponse;
import com.likelion13.artium.domain.user.dto.response.UserParticipateResponse;
import com.likelion13.artium.domain.user.dto.response.UserResponse;
import com.likelion13.artium.domain.user.dto.response.UserSummaryResponse;
import com.likelion13.artium.domain.user.entity.Age;
import com.likelion13.artium.domain.user.entity.FormatPreference;
import com.likelion13.artium.domain.user.entity.Gender;
import com.likelion13.artium.domain.user.entity.MoodPreference;
import com.likelion13.artium.domain.user.entity.Role;
import com.likelion13.artium.domain.user.entity.SortBy;
import com.likelion13.artium.domain.user.entity.ThemePreference;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.exception.UserErrorCode;
import com.likelion13.artium.domain.user.mapper.UserMapper;
import com.likelion13.artium.domain.user.mapping.UserLike;
import com.likelion13.artium.domain.user.repository.UserLikeRepository;
import com.likelion13.artium.domain.user.repository.UserRepository;
import com.likelion13.artium.global.ai.embedding.service.EmbeddingService;
import com.likelion13.artium.global.ai.vector.VectorUtils;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.page.mapper.PageMapper;
import com.likelion13.artium.global.page.response.PageResponse;
import com.likelion13.artium.global.qdrant.entity.CollectionName;
import com.likelion13.artium.global.qdrant.service.QdrantService;
import com.likelion13.artium.global.s3.entity.PathName;
import com.likelion13.artium.global.s3.exception.S3ErrorCode;
import com.likelion13.artium.global.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final S3Service s3Service;
  private final ExhibitionRepository exhibitionRepository;
  private final PieceRepository pieceRepository;
  private final UserLikeRepository userLikeRepository;
  private final EmbeddingService embeddingService;
  private final QdrantService qdrantService;
  private final PageMapper pageMapper;

  @Override
  @Transactional
  public SignUpResponse signUp(SignUpRequest request, MultipartFile image) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
    }

    if (userRepository.existsByCode(request.getCode())) {
      throw new CustomException(UserErrorCode.CODE_ALREADY_EXISTS);
    }

    // 비밀번호 인코딩
    String encodedPassword = passwordEncoder.encode(request.getPassword());
    String imageUrl;

    try {
      imageUrl = s3Service.uploadFile(PathName.PROFILE_IMAGE, image);
    } catch (Exception e) {
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }

    User user = userMapper.toUser(request, encodedPassword, imageUrl);
    User savedUser = userRepository.save(user);

    log.info("새로운 사용자 생성: {}", savedUser.getUsername());

    return userMapper.toSignUpResponse(savedUser);
  }

  @Override
  @Transactional
  public UserLikeResponse createUserLike(Long id) {

    User currentUser = getCurrentUser();

    User targetUser =
        userRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (currentUser.getId().equals(targetUser.getId())) {
      throw new CustomException(UserErrorCode.CANNOT_LIKE_SELF);
    }

    try {
      UserLike userLike = UserLike.builder().liker(currentUser).liked(targetUser).build();
      userLikeRepository.save(userLike);
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage() != null && e.getMessage().contains("uq_user_like_liked_liker")) {
        throw new CustomException(UserErrorCode.ALREADY_LIKED);
      }
      throw e;
    }
    log.info(
        "새로운 사용자 좋아요 생성 - 좋아요를 보낸 사용자: {}, 좋아요를 받은 사용자: {}",
        currentUser.getNickname(),
        targetUser.getNickname());
    return userMapper.toUserLikeResponse(currentUser.getCode(), targetUser.getCode());
  }

  @Override
  @Transactional(readOnly = true)
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.error("인증 실패 - 인증 정보 없음");
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    Object principal = authentication.getPrincipal();
    String username = "";

    try {
      switch (principal) {
        case OAuth2User oauthUser -> {
          Object email = oauthUser.getAttribute("email");
          if (email != null) {
            username = (String) email;
          } else {
            Map<String, Object> kakaoAccount = oauthUser.getAttribute("kakao_account");
            if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
              username = (String) kakaoAccount.get("email");
            }
          }
        }
        case String str -> username = str;
        case UserDetails userDetails -> username = userDetails.getUsername();
        default -> {
          log.error("인증 실패 - Principal 타입 알 수 없음: {}", principal.getClass());
          throw new CustomException(UserErrorCode.UNAUTHORIZED);
        }
      }
    } catch (Exception e) {
      log.error("인증 정보 추출 중 오류", e);
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    if (username == null || username.isBlank()) {
      log.error("인증 실패 - 추출된 username이 null 또는 빈 문자열");
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    log.debug("JWT에서 추출한 email: {}", username);

    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUser() {

    User currentUser = getCurrentUser();

    return userMapper.toUserResponse(currentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public Boolean isFirstLogin() {

    User currentUser = getCurrentUser();

    boolean hasTheme =
        currentUser.getThemePreferences() != null && !currentUser.getThemePreferences().isEmpty();
    boolean hasMood =
        currentUser.getMoodPreferences() != null && !currentUser.getMoodPreferences().isEmpty();
    boolean hasFormat =
        currentUser.getFormatPreferences() != null && !currentUser.getFormatPreferences().isEmpty();

    boolean hasAge = currentUser.getAge() != null;
    boolean hasGender = currentUser.getGender() != null;
    boolean hasCode = currentUser.getCode() != null && !currentUser.getCode().isBlank();

    // 하나라도 없으면 첫 로그인
    return !(hasTheme && hasMood && hasFormat && hasAge && hasGender && hasCode);
  }

  @Override
  @Transactional(readOnly = true)
  public Boolean checkCodeDuplicated(String code) {

    boolean exists = userRepository.existsByCode(code);

    log.info("코드 중복 체크 - userId: {}, code: {}, exists: {}", getCurrentUser().getId(), code, exists);
    return exists;
  }

  @Override
  @Transactional
  public String updateUser(UpdateUserRequest request, MultipartFile profileImage) {
    User user = getCurrentUser();
    String newCode = request.getCode();

    if (newCode != null && userRepository.existsByCodeAndIdNot(newCode, user.getId())) {
      log.error("코드 중복 시도 - userId: {}, code: {}", user.getId(), newCode);
      throw new CustomException(UserErrorCode.CODE_ALREADY_EXISTS);
    }
    if (profileImage != null && !profileImage.isEmpty()) {
      try {
        String oldImageUrl = user.getProfileImageUrl();
        String newImageUrl = s3Service.uploadFile(PathName.PROFILE_IMAGE, profileImage);

        if (oldImageUrl != null
            && !oldImageUrl.isEmpty()
            && !oldImageUrl.startsWith("http://k.kakaocdn.net")) {
          s3Service.deleteFile(oldImageUrl);
        }
        user.updateProfileImageUrl(newImageUrl);
      } catch (Exception e) {
        log.error("S3 파일 업로드 실패(교체) - userId: {}", user.getId(), e);
        throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
      }
    }

    user.updateUser(request.getNickname(), newCode, request.getIntroduction());

    log.info("사용자 정보 변경 - userId: {}, code: {}", user.getId(), newCode);
    return "사용자 정보 변경 성공";
  }

  @Override
  @Transactional
  public String updateUserInfo(UpdateUserInfoRequest request) {
    User user = getCurrentUser();
    String newNickname = request.getNickname();
    String newCode = request.getCode();

    if (newCode != null && userRepository.existsByCodeAndIdNot(newCode, user.getId())) {
      log.error("코드 중복 시도 - userId: {}, code: {}", user.getId(), newCode);
      throw new CustomException(UserErrorCode.CODE_ALREADY_EXISTS);
    }

    user.updateUserInfo(newCode, newNickname);
    log.info(
        "사용자 정보 변경 - userId: {}, newCode: {}, newNickname: {}", user.getId(), newCode, newNickname);

    return "newCode: " + newCode + ", newNickname: " + newNickname;
  }

  @Override
  @Transactional
  public String updateContact(UpdateContactRequest request) {
    User user = getCurrentUser();
    if ((request.getEmail() == null || request.getEmail().isBlank())
        && (request.getInstagram() == null || request.getInstagram().isBlank())) {
      throw new CustomException(UserErrorCode.INVALID_INPUT_REQUEST);
    }
    user.updateContact(request.getEmail(), request.getInstagram());

    log.info(
        "사용자 연락 정보 변경 - userId: {}, email: {}, instagram: {}",
        user.getId(),
        request.getEmail(),
        request.getInstagram());
    return "사용자 연락 정보 변경 성공";
  }

  @Override
  @Transactional
  public String updateProfileImage(MultipartFile profileImage) {

    User user = getCurrentUser();

    if (profileImage == null || profileImage.isEmpty()) {
      log.warn("프로필 이미지 변경 요청이 비어있음 - userId: {}", user.getId());
      return user.getProfileImageUrl();
    }

    String oldImageUrl = user.getProfileImageUrl();
    String newImageUrl;

    try {
      newImageUrl = s3Service.uploadFile(PathName.PROFILE_IMAGE, profileImage);

      if (oldImageUrl != null
          && !oldImageUrl.isEmpty()
          && !oldImageUrl.startsWith("http://k.kakaocdn.net")) {
        s3Service.deleteFile(oldImageUrl);
      }
    } catch (Exception e) {
      log.error("S3 파일 업로드 실패(교체) - userId: {}", user.getId(), e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }

    user.updateProfileImageUrl(newImageUrl);
    log.info("사용자 프로필 이미지 변경 - userId: {}, newImageUrl: {}", user.getId(), newImageUrl);

    return newImageUrl;
  }

  @Override
  @Transactional
  public String updateUserParticipation(Long exhibitionId) {

    User user = getCurrentUser();

    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

    ExhibitionParticipant participant =
        user.getExhibitionParticipants().stream()
            .filter(ep -> ep.getExhibition().getId().equals(exhibitionId))
            .findFirst()
            .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_ACCESS_DENIED));

    exhibition.getExhibitionParticipants().add(participant);
    participant.updateStatus(ParticipateStatus.APPROVED);

    log.info(
        "사용자 전시 참여 상태 변경 - userId: {}, exhibitionId: {}, newStatus: {}",
        user.getId(),
        exhibitionId,
        ParticipateStatus.APPROVED);
    return "전시 참여 상태가 APPROVED로 변경되었습니다.";
  }

  @Override
  @Transactional
  public String deleteUser() {

    User user = getCurrentUser();
    user.softDelete();

    log.info("사용자 소프트 삭제 - userId: {}", user.getId());
    return "사용자 계정이 성공적으로 삭제(soft delete)되었습니다.";
  }

  @Override
  @Transactional
  public UserLikeResponse deleteUserLike(Long id) {

    User user = getCurrentUser();

    User targetUser =
        userRepository
            .findById(id)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (user.getId().equals(targetUser.getId())) {
      throw new CustomException(UserErrorCode.CANNOT_LIKE_SELF);
    }

    UserLike userLike =
        user.getLikedUsers().stream()
            .filter(ul -> ul.getLiked().getId().equals(targetUser.getId()))
            .findFirst()
            .orElseThrow(() -> new CustomException(UserErrorCode.LIKE_NOT_FOUND));

    user.getLikedUsers().remove(userLike);
    targetUser.getLikedByUsers().remove(userLike);

    return userMapper.toUserLikeResponse(user.getCode(), targetUser.getCode());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDetailResponse> getAllUsers() {

    List<User> users = userRepository.findAll();

    return users.stream().map(userMapper::toUserDetailResponse).toList();
  }

  @Override
  @Transactional
  public String approvePiece(Long pieceId) {
    User user = getCurrentUser();

    if (!user.getRole().equals(Role.ROLE_ADMIN)) {
      log.error("요청 사용자 역할 : userRole: {}", user.getRole());
      throw new CustomException(UserErrorCode.FORBIDDEN);
    }

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    if (!piece.getSaveStatus().equals(SaveStatus.APPLICATION)
        || !piece.getProgressStatus().equals(ProgressStatus.WAITING)) {
      log.error("작품 신청 상태가 아님 : pieceId: {}", pieceId);
      throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
    }

    String content = (piece.getTitle() + "\n\n" + piece.getDescription()).trim();
    float[] vector = embeddingService.embed(content);

    qdrantService.upsertPiecePoint(pieceId, vector, piece, CollectionName.PIECE);

    piece.updateProgressStatus(ProgressStatus.REGISTERED);

    log.info("작품 등록 승인 : pieceId: {}", pieceId);
    return pieceId + "번 작품 등록을 승인했습니다.";
  }

  @Override
  @Transactional
  public String rejectPiece(Long pieceId) {
    User user = getCurrentUser();

    if (!user.getRole().equals(Role.ROLE_ADMIN)) {
      log.error("요청 사용자 역할 : userRole: {}", user.getRole());
      throw new CustomException(UserErrorCode.FORBIDDEN);
    }

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    if (!piece.getSaveStatus().equals(SaveStatus.APPLICATION)
        || !piece.getProgressStatus().equals(ProgressStatus.WAITING)) {
      log.error("작품 신청 상태가 아님 : pieceId: {}", pieceId);
      throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
    }

    piece.updateProgressStatus(ProgressStatus.UNREGISTERED);

    log.info("작품 등록 거절 : pieceId: {}", pieceId);
    return pieceId + "번 작품 등록을 거절했습니다.";
  }

  @Override
  @Transactional
  public String setPreferences(
      Gender gender,
      Age age,
      List<ThemePreference> themePreferences,
      List<MoodPreference> moodPreferences,
      List<FormatPreference> formatPreferences) {
    User user = getCurrentUser();

    user.updatePreferences(gender, age, themePreferences, moodPreferences, formatPreferences);

    float[] vector = makeEmbeddingPreference(themePreferences, moodPreferences, formatPreferences);
    qdrantService.upsertUserPoint(user.getId(), vector, user, CollectionName.USER);

    log.info("사용자 관심사 설정 성공 - userId: {}", user.getId());
    return "사용자 관심사 설정에 성공했습니다.";
  }

  @Override
  @Transactional(readOnly = true)
  public PreferenceResponse getPreferences() {
    User user = getCurrentUser();
    log.info("사용자 관심사 조회 성공 - userId: {}", user.getId());
    return userMapper.toPreferenceResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> getKeywords() {
    User user = getCurrentUser();

    List<ThemePreference> themePrefs =
        user.getThemePreferences() != null ? user.getThemePreferences() : List.of();
    List<MoodPreference> moodPrefs =
        user.getMoodPreferences() != null ? user.getMoodPreferences() : List.of();
    List<FormatPreference> formatPrefs =
        user.getFormatPreferences() != null ? user.getFormatPreferences() : List.of();
    float[] userVector = makeEmbeddingPreference(themePrefs, moodPrefs, formatPrefs);
    if (userVector == null) {
      log.info("사용자 취향 정보가 없어 키워드 추천을 건너뜁니다 - userId: {}", user.getId());
      return List.of();
    }

    List<Map<String, Object>> pieceResults =
        qdrantService.search(userVector, 50, List.of(), CollectionName.PIECE, false);

    List<Map<String, Object>> exhibitionResults =
        qdrantService.search(userVector, 50, List.of(), CollectionName.EXHIBITION, false);

    List<String> keywords = new ArrayList<>();

    for (Map<String, Object> m : pieceResults) {
      Map<String, Object> payload = (Map<String, Object>) m.get("payload");
      if (payload == null) continue;
      Object kw = payload.get("keywords");
      if (kw instanceof List<?> list) {
        for (Object o : list) {
          if (o != null) keywords.add(o.toString());
        }
      }
    }
    for (Map<String, Object> m : exhibitionResults) {
      Map<String, Object> payload = (Map<String, Object>) m.get("payload");
      if (payload == null) continue;
      Object kw = payload.get("keywords");
      if (kw instanceof List<?> list) {
        for (Object o : list) {
          if (o != null) keywords.add(o.toString());
        }
      }
    }

    List<String> distinctKeywords = keywords.stream().distinct().toList();

    log.info("사용자 관심사 기반 키워드 리스트 조회 - userId: {}, keywords: {}", user.getId(), distinctKeywords);
    return distinctKeywords;
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<UserSummaryResponse> getLikes(Pageable pageable) {
    Long userId = getCurrentUser().getId();

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "createdAt"));

    Page<UserSummaryResponse> page =
        userLikeRepository
            .findLikedUserByLikerId(userId, sortedPageable)
            .map(userMapper::toUserSummaryResponse);

    log.info("사용자 좋아요 리스트 조회 성공 - userId: {}", userId);
    return pageMapper.toUserSummaryPageResponse(page);
  }

  @Override
  public UserSummaryResponse getUserProfile(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    log.info("사용자 프로필 조회 성공 - userId: {}", userId);
    return userMapper.toUserSummaryResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryResponse> getUserProfilesByCode(String code) {

    List<User> userList = userRepository.findByCodeContaining(code);
    List<UserSummaryResponse> responseList =
        userList.stream().map(userMapper::toUserSummaryResponse).toList();

    log.info("코드 포함 사용자 검색 성공 - code: {}, totalElements: {}", code, responseList.size());
    return responseList;
  }

  @Override
  @Transactional(readOnly = true)
  public Integer getUserParticipationCount(ParticipateStatus status) {
    User user = getCurrentUser();

    int count =
        (int)
            user.getExhibitionParticipants().stream()
                .filter(ep -> ep.getParticipateStatus() == status)
                .count();

    log.info("사용자 전시 참여 개수 조회 - userId: {}, status: {}, count: {}", user.getId(), status, count);
    return count;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserParticipateResponse> getUserParticipation(ParticipateStatus status) {

    User user = getCurrentUser();

    List<UserParticipateResponse> responses =
        user.getExhibitionParticipants().stream()
            .filter(ep -> ep.getParticipateStatus() == status)
            .map(userMapper::toUserParticipateResponse)
            .toList();

    log.info(
        "사용자 전시 참여 정보 조회 성공 - userId: {}, status: {}, count: {}",
        user.getId(),
        status,
        responses.size());

    return responses;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryResponse> getUserListByKeyword(String keyword) {
    User currentUser = getCurrentUser();

    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }
    keyword = keyword.trim();

    List<User> users;
    if (keyword.startsWith("@")) {
      String codeKeyword = keyword.substring(1).trim();
      if (codeKeyword.isEmpty()) {
        return List.of();
      }
      users = userRepository.findByCodeContaining(codeKeyword);
      log.info("코드로 사용자 조회 성공 - 요청한 사용자: {}, 키워드: {}", currentUser.getNickname(), codeKeyword);
    } else {
      users = userRepository.findByNicknameContaining(keyword);
      log.info("닉네임으로 사용자 조회 성공 - 요청한 사용자: {}, 키워드: {}", currentUser.getNickname(), keyword);
    }

    return users.stream()
        .filter(u -> !u.getId().equals(currentUser.getId()))
        .map(userMapper::toUserSummaryResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public UserContactResponse getUserContact(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    log.info("사용자 연락처 조회 성공 - userId: {}", userId);
    return userMapper.toUserContactResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public CreatorResponse getCreatorInfo(Long userId) {
    Long likerId = getCurrentUser().getId();
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    log.info("크리에이터 정보 조회 성공 - requestUserId: {}, responseUserId: {}", likerId, userId);
    if (userLikeRepository.existsByLiker_IdAndLiked_Id(likerId, userId)) {
      return userMapper.toCreatorResponse(user, true);
    } else {
      return userMapper.toCreatorResponse(user, false);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Boolean getContactStatus() {
    User user = getCurrentUser();
    return org.springframework.util.StringUtils.hasText(user.getEmail())
        || org.springframework.util.StringUtils.hasText(user.getInstagram());
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<CreatorFeedResponse> getRecommendations(SortBy sortBy, Pageable pageable) {
    User user = getCurrentUser();
    Page<CreatorFeedResponse> page;

    switch (sortBy) {
      case HOTTEST:
        page =
            userRepository
                .findHottestCreators(user.getId(), pageable)
                .map(
                    u ->
                        userMapper.toCreatorFeedResponse(
                            u,
                            userLikeRepository.existsByLiker_IdAndLiked_Id(
                                user.getId(), u.getId())));
        log.info("가장 인기 있는 크리에이터 리스트 페이지 조회 성공");
        break;
      case LATEST_OPEN:
        LocalDate cutoffDate = LocalDate.now().minusDays(7);
        page =
            userRepository
                .findRecentOngoingExhibitionUsers(
                    user.getId(), cutoffDate, ExhibitionStatus.ONGOING, pageable)
                .map(
                    u ->
                        userMapper.toCreatorFeedResponse(
                            u,
                            userLikeRepository.existsByLiker_IdAndLiked_Id(
                                user.getId(), u.getId())));
        log.info("최근 전시 오픈한 크리에이터 리스트 페이지 조회 성공");
        break;

      case PEER_GROUP:
        page =
            userRepository
                .findSameAgeUsers(
                    user.getId(),
                    user.getAge(),
                    ProgressStatus.WAITING,
                    ProgressStatus.UNREGISTERED,
                    pageable)
                .map(
                    u ->
                        userMapper.toCreatorFeedResponse(
                            u,
                            userLikeRepository.existsByLiker_IdAndLiked_Id(
                                user.getId(), u.getId())));
        log.info("나와 비슷한 연령대의 크리에이터 리스트 페이지 조회 성공");
        break;

      default:
        throw new CustomException(UserErrorCode.INVALID_SORT_TYPE);
    }

    return pageMapper.toCreatorFeedPageResponse(page);
  }

  private float[] makeEmbeddingPreference(
      List<ThemePreference> themePreferences,
      List<MoodPreference> moodPreferences,
      List<FormatPreference> formatPreferences) {
    String themes =
        themePreferences.stream().map(ThemePreference::getKo).collect(Collectors.joining(", "));
    String moods =
        moodPreferences.stream().map(MoodPreference::getKo).collect(Collectors.joining(", "));
    String formats =
        formatPreferences.stream().map(FormatPreference::getKo).collect(Collectors.joining(", "));

    List<float[]> vecs = new ArrayList<>();
    List<Double> ws = new ArrayList<>();

    BiConsumer<String, Double> add =
        (text, w) -> {
          if (text != null && !text.isBlank()) {
            float[] v = embeddingService.embed(text);
            if (v != null) {
              vecs.add(VectorUtils.normalize(v));
              ws.add(w);
            }
          }
        };

    add.accept("themes: " + themes, 0.80);
    add.accept("moods: " + moods, 0.10);
    add.accept("formats: " + formats, 0.10);

    float[] acc = null;
    for (int i = 0; i < vecs.size(); i++) {
      acc =
          (acc == null)
              ? VectorUtils.scale(vecs.get(i), ws.get(i))
              : VectorUtils.addScaled(acc, vecs.get(i), 1.0, ws.get(i));
    }

    return (acc == null) ? null : VectorUtils.normalize(acc);
  }
}
