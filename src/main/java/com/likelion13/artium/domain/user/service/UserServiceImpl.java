/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.service;

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

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.piece.entity.SaveStatus;
import com.likelion13.artium.domain.piece.exception.PieceErrorCode;
import com.likelion13.artium.domain.piece.repository.PieceRepository;
import com.likelion13.artium.domain.user.dto.request.SignUpRequest;
import com.likelion13.artium.domain.user.dto.response.LikeResponse;
import com.likelion13.artium.domain.user.dto.response.PreferenceResponse;
import com.likelion13.artium.domain.user.dto.response.SignUpResponse;
import com.likelion13.artium.domain.user.dto.response.UserDetailResponse;
import com.likelion13.artium.domain.user.dto.response.UserSummaryResponse;
import com.likelion13.artium.domain.user.entity.Age;
import com.likelion13.artium.domain.user.entity.FormatPreference;
import com.likelion13.artium.domain.user.entity.Gender;
import com.likelion13.artium.domain.user.entity.MoodPreference;
import com.likelion13.artium.domain.user.entity.Role;
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
  private final EmbeddingService embeddingService;
  private final QdrantService qdrantService;
  private final UserLikeRepository userLikeRepository;
  private final PageMapper pageMapper;

  @Override
  @Transactional
  public SignUpResponse signUp(SignUpRequest request, MultipartFile image) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
    }

    if (userRepository.existsByNickname(request.getNickname())) {
      throw new CustomException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
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
  public LikeResponse createUserLike(Long userId) {

    User currentUser = getCurrentUser();

    User targetUser =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (currentUser.getId().equals(targetUser.getId())) {
      throw new CustomException(UserErrorCode.CANNOT_LIKE_SELF);
    }

    try {
      UserLike userLike = UserLike.builder().liker(currentUser).liked(targetUser).build();

      currentUser.getLikedUsers().add(userLike);
      targetUser.getLikedByUsers().add(userLike);

      return userMapper.toLikeResponse(currentUser.getNickname(), targetUser.getNickname());
    } catch (DataIntegrityViolationException e) {
      throw new CustomException(UserErrorCode.ALREADY_LIKED);
    }
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
  public UserDetailResponse getUserDetail() {

    User currentUser = getCurrentUser();

    return userMapper.toUserDetailResponse(currentUser);
  }

  @Override
  public Boolean checkNicknameDuplicated(String nickname) {

    boolean exists = userRepository.existsByNickname(nickname);

    log.info("닉네임 중복 체크 - nickname: {}, exists: {}", nickname, exists);
    return exists;
  }

  @Override
  @Transactional
  public String updateNickname(String newNickname) {
    User user = getCurrentUser();

    if (userRepository.existsByNickname(newNickname)) {
      log.error("닉네임 중복 시도 - userId: {}, nickname: {}", user.getId(), newNickname);
      throw new CustomException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    user.updateNickname(newNickname);
    log.info("사용자 닉네임 변경 - userId: {}, newNickname: {}", user.getId(), newNickname);

    return newNickname;
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
  public LikeResponse deleteUserLike(Long userId) {

    User user = getCurrentUser();

    User targetUser =
        userRepository
            .findById(userId)
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

    return userMapper.toLikeResponse(user.getNickname(), targetUser.getNickname());
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

    if (!user.getRole().equals(Role.ADMIN)) {
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    if (!piece.getSaveStatus().equals(SaveStatus.APPLICATION)
        || !piece.getProgressStatus().equals(ProgressStatus.WAITING)) {
      throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
    }

    String content = (piece.getTitle() + "\n\n" + piece.getDescription()).trim();
    float[] vector = embeddingService.embed(content);

    qdrantService.upsertPiecePoint(pieceId, vector, piece, CollectionName.PIECE);

    piece.updateProgressStatus(ProgressStatus.REGISTERED);

    return pieceId + "번 작품 등록을 승인했습니다.";
  }

  @Override
  @Transactional
  public String rejectPiece(Long pieceId) {
    User user = getCurrentUser();

    if (!user.getRole().equals(Role.ADMIN)) {
      throw new CustomException(UserErrorCode.UNAUTHORIZED);
    }

    Piece piece =
        pieceRepository
            .findById(pieceId)
            .orElseThrow(() -> new CustomException(PieceErrorCode.PIECE_NOT_FOUND));

    if (!piece.getSaveStatus().equals(SaveStatus.APPLICATION)
        || !piece.getProgressStatus().equals(ProgressStatus.WAITING)) {
      throw new CustomException(PieceErrorCode.INVALID_APPLICATION);
    }

    piece.updateProgressStatus(ProgressStatus.UNREGISTERED);

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

    return "사용자 관심사 설정에 성공했습니다.";
  }

  @Override
  public PreferenceResponse getPreferences() {
    return userMapper.toPreferenceResponse(getCurrentUser());
  }

  @Override
  public PageResponse<UserSummaryResponse> getLikes(Pageable pageable) {

    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "createdAt"));

    Page<UserSummaryResponse> page =
        userLikeRepository
            .findLikedUserByLikerId(getCurrentUser().getId(), sortedPageable)
            .map(userMapper::toUserSummaryResponse);

    return pageMapper.toUserSummaryPageResponse(page);
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
