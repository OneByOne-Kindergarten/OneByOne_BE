package com.onebyone.kindergarten.domain.user.service;

import com.onebyone.kindergarten.domain.user.dto.*;
import com.onebyone.kindergarten.domain.user.dto.request.*;
import com.onebyone.kindergarten.domain.user.dto.response.AdminUserResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.response.AppleUserResponse;
import com.onebyone.kindergarten.domain.user.dto.response.KakaoUserResponse;
import com.onebyone.kindergarten.domain.user.entity.EmailCertification;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.EmailCertificationType;
import com.onebyone.kindergarten.domain.user.enums.NotificationSetting;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import com.onebyone.kindergarten.domain.user.repository.EmailCertificationRepository;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final EmailCertificationRepository emailCertificationRepository;
  private static Logger logger = LoggerFactory.getLogger(UserService.class);

  @Transactional
  public JwtUserInfoDto signUp(SignUpRequestDTO request) {
    if (isExistedEmail(request.getEmail())) {
      throw new BusinessException(ErrorCodes.ALREADY_EXIST_EMAIL);
    }

    EmailCertification emailCertification =
        emailCertificationRepository.findByEmail(request.getEmail());
    if (emailCertification == null || !emailCertification.isCertificated()) {
      throw new BusinessException(ErrorCodes.FAILED_EMAIL_CERTIFICATION_EXCEPTION);
    }

    String encodedPassword = encodePassword(request.getPassword());
    User user = userRepository.save(request.toEntity(encodedPassword));

    return new JwtUserInfoDto(user.getId(), user.getRole());
  }

  @Transactional(readOnly = true)
  public boolean isExistedEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  @Transactional
  public JwtUserInfoDto signIn(SignInRequestDTO request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_USER));

    switch (user.getStatus()) {
      case ACTIVE:
        validatePassword(request.getPassword(), user);
        updateFcmTokenIfPresent(request, user);

        return new JwtUserInfoDto(user.getId(), user.getRole());
      case DELETED:
        validatePassword(request.getPassword(), user);
        user.restore();
        updateFcmTokenIfPresent(request, user);

        return new JwtUserInfoDto(user.getId(), user.getRole());
      case SUSPENDED:
        throw new BusinessException(ErrorCodes.SUSPENDED_USER_EXCEPTION);
      case ANONYMOUS:
      default:
        throw new BusinessException(ErrorCodes.LOGIN_NOT_ALLOWED_ANONYMOUS);
    }
  }

  @Transactional
  public void changeNickname(Long userId, ModifyUserNicknameRequestDTO request) {
    User user = getUserById(userId);

    // 현재 닉네임과 동일한지 확인
    if (user.getNickname().equals(request.getNewNickname())) {
      return; // 동일한 닉네임이면 변경하지 않음
    }

    user.changeNickname(request.getNewNickname());
  }

  @Transactional
  public void changePassword(Long userId, ModifyUserPasswordRequestDTO request) {
    User user = getUserById(userId);
    validatePassword(request.getCurrentPassword(), user);

    user.changePassword(passwordEncoder.encode(request.getNewPassword()));
  }

  @Transactional
  public void withdraw(Long userId) {
    User user = getUserById(userId);
    user.withdraw();
  }

  public User getUserByEmail(String email) {
    return userRepository
        .findByEmailAndStatus(email, UserStatus.ACTIVE)
        .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
  }

  public void addCareer(User user, LocalDate startDate, LocalDate endDate) {
    int careerMonths = calculateCareerMonths(user, startDate, endDate, true);
    user.updateCareer(String.valueOf(careerMonths));
  }

  public void removeCareer(User user, LocalDate startDate, LocalDate endDate) {
    int careerMonths = calculateCareerMonths(user, startDate, endDate, false);
    user.updateCareer(String.valueOf(careerMonths));
  }

  public User getUserById(Long userId) {
    return userRepository
        .findByIdAndStatus(userId, UserStatus.ACTIVE)
        .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
  }

  public UserDTO getUserToDTO(Long userId) {
    return UserDTO.from(
        userRepository
            .findIdWithKindergarten(userId)
            .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL)));
  }

  @Transactional
  public User signUpByKakao(KakaoUserResponse userResponse, String fcmToken) {
    String email = userResponse.getKakao_account().getEmail();

    Optional<User> user = userRepository.findByEmail(email);

    if (user.isPresent()) {
      User getUser = user.get();

      switch (getUser.getStatus()) {
        case ACTIVE:
          if (StringUtils.hasText(fcmToken)) {
            getUser.updateFcmToken(fcmToken);
          }
          return getUser;
        case DELETED:
          getUser.restore();
          if (StringUtils.hasText(fcmToken)) {
            getUser.updateFcmToken(fcmToken);
          }
          return getUser;
        case SUSPENDED:
          throw new BusinessException(ErrorCodes.SUSPENDED_USER_EXCEPTION);
      }
    }

    // 신규 가입자
    String nickname;
    if (userResponse.getKakao_account().getProfile() != null
        && userResponse.getKakao_account().getProfile().getNickname() != null
        && !userResponse.getKakao_account().getProfile().getNickname().trim().isEmpty()) {
      String originalNickname = userResponse.getKakao_account().getProfile().getNickname().trim();
      nickname =
          originalNickname.length() > 10 ? originalNickname.substring(0, 10) : originalNickname;
    } else {
      // "카카오" (3글자) + ID 마지막 6자리 = 최대 9글자
      String idSuffix = String.valueOf(userResponse.getId());
      if (idSuffix.length() > 6) {
        idSuffix = idSuffix.substring(idSuffix.length() - 6);
      }
      nickname = "카카오" + idSuffix;
    }

    // 새로운 사용자 생성
    String dummyPassword = encodePassword("kakao_" + userResponse.getId());
    // 프로필 이미지 업데이트
    String profileImageUrl =
        userResponse.getKakao_account().getProfile() != null
            ? userResponse.getKakao_account().getProfile().getProfile_image_url()
            : null;

    User registerUser =
        User.registerKakao(
            email,
            dummyPassword,
            userResponse.getId(),
            nickname,
            UserRole.GENERAL,
            profileImageUrl);

    userRepository.save(registerUser);

    return registerUser;
  }

  /*
    @Transactional
    public User signUpByNaver(NaverUserResponse userResponse) {
      String email = userResponse.getResponse().getEmail();

      // 활성 사용자 확인
      Optional<User> activeUser = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
      if (activeUser.isPresent()) {
        return activeUser.get();
      }

      // 탈퇴된 사용자 확인 및 복구
      Optional<User> deletedUser = userRepository.findByEmailAndStatus(email, UserStatus.DELETED);
      if (deletedUser.isPresent()) {
        User user = deletedUser.get();
        user.restore();

        // 소셜 로그인 정보 업데이트
        if (userResponse.getResponse().getProfile_image() != null) {
          user.updateProfileImageUrl(userResponse.getResponse().getProfile_image());
        }

        return user;
      }

      // 새로운 사용자 생성
      String dummyPassword = encodePassword("naver_" + userResponse.getResponse().getId());

      // 네이버 닉네임 길이 제한 처리
      String naverNickname = userResponse.getResponse().getNickname();
      if (naverNickname != null && !naverNickname.trim().isEmpty()) {
        naverNickname = naverNickname.trim();
        if (naverNickname.length() > 10) {
          naverNickname = naverNickname.substring(0, 10);
        }
      } else {
        // 닉네임이 없는 경우 기본 닉네임 생성: "네이버" + ID 마지막 6자리
        String idSuffix = userResponse.getResponse().getId();
        if (idSuffix != null && idSuffix.length() > 6) {
          idSuffix = idSuffix.substring(idSuffix.length() - 6);
        }
        naverNickname = "네이버" + (idSuffix != null ? idSuffix : "사용자");
      }

      User user =
          User.registerNaver(
              email,
              dummyPassword,
              userResponse.getResponse().getId(),
              naverNickname,
              UserRole.GENERAL,
              userResponse.getResponse().getProfile_image());

      userRepository.save(user);

      return user;
    }
  */
  @Transactional
  public User signUpByApple(AppleUserResponse userResponse, String fcmToken) {
    String appleUserId = userResponse.getSub();
    String providedEmail = userResponse.getEmail();

    // 이메일 숨기기 처리: 시스템 이메일 생성
    String systemEmail;
    if (providedEmail != null && providedEmail.endsWith("@privaterelay.appleid.com")) {
      // 익명 이메일인 경우 시스템 이메일 생성
      systemEmail =
          "apple_user_"
              + appleUserId.substring(0, Math.min(appleUserId.length(), 10))
              + "@kindergarten.system";
    } else if (providedEmail != null) {
      // 실제 이메일인 경우 그대로 사용
      systemEmail = providedEmail;
    } else {
      // 이메일이 없는 경우 시스템 이메일 생성
      systemEmail =
          "apple_user_"
              + appleUserId.substring(0, Math.min(appleUserId.length(), 10))
              + "@kindergarten.system";
    }

    // 활성 사용자 확인
    Optional<User> user = userRepository.findByEmail(systemEmail);

    if (user.isPresent()) {
      User getUser = user.get();

      switch (getUser.getStatus()) {
        case ACTIVE:
          if (StringUtils.hasText(fcmToken)) {
            getUser.updateFcmToken(fcmToken);
          }
          return getUser;
        case DELETED:
          getUser.restore();
          if (StringUtils.hasText(fcmToken)) {
            getUser.updateFcmToken(fcmToken);
          }
          return getUser;
        case SUSPENDED:
          throw new BusinessException(ErrorCodes.SUSPENDED_USER_EXCEPTION);
      }
    }

    // 새로운 사용자 생성
    String dummyPassword = encodePassword("apple_" + appleUserId);
    String nickname;
    if (userResponse.getName() != null && !userResponse.getName().trim().isEmpty()) {
      String originalName = userResponse.getName().trim();
      nickname = originalName.length() > 10 ? originalName.substring(0, 10) : originalName;
    } else {
      // "애플" (2글자) + 사용자 ID 마지막 6자리 = 최대 8글자
      String idSuffix =
          appleUserId.length() > 6 ? appleUserId.substring(appleUserId.length() - 6) : appleUserId;
      nickname = "애플" + idSuffix;
    }

    User registerUser =
        User.registerApple(systemEmail, dummyPassword, appleUserId, nickname, UserRole.GENERAL);

    userRepository.save(registerUser);

    return registerUser;
  }

  @Transactional
  public void saveSignUpCertification(EmailCertificationRequestDTO request, String certification) {
    String email = request.getEmail();

    if (userRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCodes.ALREADY_EXIST_EMAIL);
    }

    EmailCertification emailCert =
        emailCertificationRepository.findByEmailAndType(email, EmailCertificationType.EMAIL);

    if (emailCert == null) {
      emailCert =
          EmailCertification.builder().email(email).type(EmailCertificationType.EMAIL).build();
    }

    emailCert.updateCode(certification);
    emailCert.markUncertificated();

    emailCertificationRepository.save(emailCert);
  }

  @Transactional
  public void updateHomeShortcut(Long userId, HomeShortcutsDto homeShortcutsDto) {
    User user = getUserById(userId);
    user.updateHomeShortcut(homeShortcutsDto.toJson());
  }

  @Transactional
  public void savePasswordCertification(
      EmailCertificationRequestDTO request, String certification) {
    String email = request.getEmail();

    if (!userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(ErrorCodes.NOT_FOUND_USER);
    }

    EmailCertification passwordCert =
        emailCertificationRepository.findByEmailAndType(
            email, EmailCertificationType.TEMPORARY_PASSWORD);

    if (passwordCert == null) {
      passwordCert =
          EmailCertification.builder()
              .email(email)
              .type(EmailCertificationType.TEMPORARY_PASSWORD)
              .build();
    }

    passwordCert.updateCode(certification);
    passwordCert.markUncertificated();

    emailCertificationRepository.save(passwordCert);
  }

  @Transactional
  public void checkEmailCertification(CheckEmailCertificationRequestDTO request) {
    EmailCertification emailCertification =
        emailCertificationRepository.findByEmailAndCodeAndTypeAndDeletedAtIsNull(
            request.getEmail(), request.getCertification(), EmailCertificationType.EMAIL);

    if (emailCertification == null) {
      throw new BusinessException(ErrorCodes.NOT_FOUND_EMAIL);
    }

    emailCertification.completeCertification();
  }

  @Transactional
  public void updateUserRole(Long userId, UpdateUserRoleRequestDTO request) {
    User user = getUserById(userId);
    user.updateUserRole(request.getRole());
  }

  public void updateTemporaryPassword(String email, String number) {
    User user = getUserByEmail(email);
    user.changePassword(passwordEncoder.encode(number));
  }

  public void checkEmailCertificationByTemporaryPassword(String email, String code) {
    EmailCertification emailCertification =
        emailCertificationRepository.findByEmailAndCodeAndTypeAndDeletedAtIsNull(
            email, code, EmailCertificationType.TEMPORARY_PASSWORD);

    if (emailCertification == null) {
      throw new BusinessException(ErrorCodes.NOT_FOUND_CERTIFICATION);
    }

    if (!emailCertification.getCode().equals(code)) {
      throw new BusinessException(ErrorCodes.CERTIFICATION_CODE_MISMATCH);
    }

    emailCertification.completeCertification();
  }

  @Transactional(readOnly = true)
  public NotificationSettingsDTO getNotificationSettings(Long userId) {
    User user = getUserById(userId);

    return NotificationSettingsDTO.builder()
        .allNotificationsEnabled(user.hasNotificationEnabled(NotificationSetting.ALL_NOTIFICATIONS))
        .communityNotificationsEnabled(
            user.hasNotificationEnabled(NotificationSetting.COMMUNITY_NOTIFICATIONS))
        .eventNotificationsEnabled(
            user.hasNotificationEnabled(NotificationSetting.EVENT_NOTIFICATIONS))
        .build();
  }

  @Transactional
  public NotificationSettingsDTO updateNotificationSettings(
      Long userId, NotificationSettingsDTO request) {
    User user = getUserById(userId);

    Set<NotificationSetting> enabledSettings = new HashSet<>();
    if (request.isAllNotificationsEnabled()) {
      enabledSettings.add(NotificationSetting.ALL_NOTIFICATIONS);
    }
    if (request.isCommunityNotificationsEnabled()) {
      enabledSettings.add(NotificationSetting.COMMUNITY_NOTIFICATIONS);
    }
    if (request.isEventNotificationsEnabled()) {
      enabledSettings.add(NotificationSetting.EVENT_NOTIFICATIONS);
    }

    user.setNotificationSettings(enabledSettings);

    return request;
  }

  @Transactional
  public void markUserAsReviewWriter(Long userId) {
    User user = getUserById(userId);
    if (!user.hasWrittenReview()) {
      user.markAsReviewWriter();
    }
  }

  /// 관리자용 - 전체 유저 조회
  @Transactional(readOnly = true)
  public Page<AdminUserResponseDTO> getAllUsers(Pageable pageable) {
    Page<User> users = userRepository.findAllUsersWithKindergarten(pageable);
    return users.map(AdminUserResponseDTO::from);
  }

  /// 관리자용 - 유저 검색
  @Transactional(readOnly = true)
  public Page<AdminUserResponseDTO> searchUsers(UserSearchDTO searchDTO, Pageable pageable) {
    Page<User> users =
        userRepository.findUsersWithFilters(
            searchDTO.getEmail(),
            searchDTO.getNickname(),
            searchDTO.getRole(),
            searchDTO.getProvider(),
            searchDTO.getStatus(),
            searchDTO.getKindergartenName(),
            searchDTO.getHasWrittenReview(),
            searchDTO.getIsRestoredUser(),
            pageable);
    return users.map(AdminUserResponseDTO::from);
  }

  @Transactional(readOnly = true)
  public AdminUserResponseDTO getUserToAdminDTO(Long userId) {
    User user =
        userRepository
            .findByIdWithKindergarten(userId)
            .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_USER));
    return AdminUserResponseDTO.from(user);
  }

  /// 관리자용 - 유저 상태 변경
  @Transactional
  public void updateUserStatus(Long userId, UpdateUserStatusRequestDTO request) {
    // 관리자 권한 확인
    User admin = getUserById(userId);
    if (!admin.getRole().equals(UserRole.ADMIN)) {
      throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
    }

    // 대상 유저 조회
    User targetUser =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));

    // 관리자는 자신의 상태를 변경할 수 없음
    if (targetUser.getRole().equals(UserRole.ADMIN)) {
      throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
    }

    // 상태 변경
    targetUser.updateStatus(request.getStatus());
  }

  @Transactional
  public void withdrawAfter30Days(LocalDateTime before30Days) {
    List<User> users = userRepository.findAllByWithdrawAfter30Days(before30Days);

    logger.debug("withdrawAfter30Days 대상 사용자 수: {}", users.size());

    users.forEach(User::withdrawAfter30Days);
  }

  /// 경력 개월 수 계산
  private int calculateCareerMonths(
      User user, LocalDate startDate, LocalDate endDate, boolean isAdding) {
    int currentCareerMonths = user.getCareer() == null ? 0 : Integer.parseInt(user.getCareer());
    long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
    return isAdding
        ? currentCareerMonths + (int) monthsBetween
        : currentCareerMonths - (int) monthsBetween;
  }

  private void validatePassword(String rawPassword, User user) {
    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new BusinessException(ErrorCodes.INVALID_PASSWORD_ERROR);
    }
  }

  private void updateFcmTokenIfPresent(SignInRequestDTO request, User user) {
    if (request.getFcmToken() != null) {
      user.updateFcmToken(request.getFcmToken());
    }
  }
}
