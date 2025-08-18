/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
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
      if (principal instanceof OAuth2User oauthUser) {
        Object email = oauthUser.getAttribute("email");
        if (email != null) {
          username = (String) email;
        } else {
          Map<String, Object> kakaoAccount = oauthUser.getAttribute("kakao_account");
          if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            username = (String) kakaoAccount.get("email");
          }
        }
      } else if (principal instanceof String str) {
        username = str;
      } else if (principal instanceof UserDetails userDetails) {
        username = userDetails.getUsername();
      } else {
        log.error("인증 실패 - Principal 타입 알 수 없음: {}", principal.getClass());
        throw new CustomException(UserErrorCode.UNAUTHORIZED);
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

        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
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

      if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
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
    String content =
        makeEmbeddingPreference(gender, age, themePreferences, moodPreferences, formatPreferences);

    float[] vector = embeddingService.embed(content);

    qdrantService.upsertUserPoint(user.getId(), vector, user, CollectionName.USER);

    log.info("사용자 관심사 설정 성공 - userId: {}", user.getId());
    return "사용자 관심사 설정에 성공했습니다.";
  }

  @Override
  public PreferenceResponse getPreferences() {
    User user = getCurrentUser();
    log.info("사용자 관심사 조회 성공 - userId: {}", user.getId());
    return userMapper.toPreferenceResponse(user);
  }

  @Override
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
  public UserContactResponse getUserContact(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    log.info("사용자 연락처 조회 성공 - userId: {}", userId);
    return userMapper.toUserContactResponse(user);
  }

  @Override
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
  public Boolean getContactStatus() {
    User user = getCurrentUser();
    return org.springframework.util.StringUtils.hasText(user.getEmail())
        || org.springframework.util.StringUtils.hasText(user.getInstagram());
  }

  @Override
  public PageResponse<CreatorFeedResponse> getRecommendations(SortBy sortBy, Pageable pageable) {
    User user = getCurrentUser();
    Page<CreatorFeedResponse> page;

    switch (sortBy) {
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
        List<ProgressStatus> statuses =
            new ArrayList<>(Arrays.asList(ProgressStatus.WAITING, ProgressStatus.UNREGISTERED));
        page =
            userRepository
                .findSameAgeUsers(user.getId(), user.getAge(), statuses, pageable)
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

  private String makeEmbeddingPreference(
      Gender gender,
      Age age,
      List<ThemePreference> themePreferences,
      List<MoodPreference> moodPreferences,
      List<FormatPreference> formatPreferences) {
    return "gender : "
        + gender.getKo()
        + "\n"
        + "age : "
        + age.getKo()
        + "\n"
        + "theme_preferences : "
        + themePreferences.stream().map(ThemePreference::getKo).toList()
        + "\n"
        + "mood_preferences : "
        + moodPreferences.stream().map(MoodPreference::getKo).toList()
        + "\n"
        + "formatPreferences : "
        + formatPreferences.stream().map(FormatPreference::getKo).toList();
  }
}
