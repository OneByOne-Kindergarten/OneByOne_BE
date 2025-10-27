package com.onebyone.kindergarten.domain.user.service;

import com.onebyone.kindergarten.domain.user.dto.*;
import com.onebyone.kindergarten.domain.user.dto.request.*;
import com.onebyone.kindergarten.domain.user.dto.response.AppleUserResponse;
import com.onebyone.kindergarten.domain.user.dto.response.KakaoUserResponse;
import com.onebyone.kindergarten.domain.user.dto.response.NaverUserResponse;
import com.onebyone.kindergarten.domain.user.entity.EmailCertification;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.NotificationSetting;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.repository.EmailCertificationRepository;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import com.onebyone.kindergarten.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.onebyone.kindergarten.domain.user.dto.response.AdminUserResponseDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailCertificationRepository emailCertificationRepository;

    @Transactional
    public String signUp(SignUpRequestDTO request) {
        if (isExistedEmail(request.getEmail())) {
            throw new BusinessException(ErrorCodes.ALREADY_EXIST_EMAIL);
        }

        EmailCertification emailCertification = emailCertificationRepository.findByEmail(request.getEmail());
        if (emailCertification == null || !emailCertification.isCertificated()) {
            throw new BusinessException(ErrorCodes.FAILED_EMAIL_CERTIFICATION_EXCEPTION);
        }

        String encodedPassword = encodePassword(request.getPassword());
        User user = userRepository.save(request.toEntity(encodedPassword));

        return user.getEmail();
    }

    @Transactional(readOnly = true)
    public boolean isExistedEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email).isPresent();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Transactional
    public String signIn(SignInRequestDTO request) {
        // 먼저 활성 사용자 확인
        Optional<User> activeUser = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail());
        if (activeUser.isPresent()) {
            User user = activeUser.get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCodes.INVALID_PASSWORD_ERROR);
            }
            
            if (request.getFcmToken() != null) {
                user.updateFcmToken(request.getFcmToken());
            }
            
            return user.getEmail();
        }
        
        // 탈퇴된 사용자 확인 및 복구
        Optional<User> deletedUser = userRepository.findByEmailAndDeletedAtIsNotNull(request.getEmail());
        if (deletedUser.isPresent()) {
            User user = deletedUser.get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCodes.INVALID_PASSWORD_ERROR);
            }
            
            // 계정 복구
            user.restore();
            
            if (request.getFcmToken() != null) {
                user.updateFcmToken(request.getFcmToken());
            }
            
            return user.getEmail();
        }

        throw new BusinessException(ErrorCodes.NOT_FOUND_EMAIL);
    }

    @Transactional
    public void changeNickname(String email, ModifyUserNicknameRequestDTO request) {
        User user = findUser(email);
        
        // 현재 닉네임과 동일한지 확인
        if (user.getNickname().equals(request.getNewNickname())) {
            return; // 동일한 닉네임이면 변경하지 않음
        }
        
        user.changeNickname(request.getNewNickname());
    }

    @Transactional
    public void changePassword(String email, ModifyUserPasswordRequestDTO request) {
        User user = findUser(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_PASSWORD_ERROR);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private User findUser(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
    }

    @Transactional
    public void withdraw(String email) {
        User user = findUser(email);
        user.withdraw();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
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

    public UserDTO getUser(String email) {
        return UserDTO.from(userRepository.findUserWithKindergarten(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL)));
    }

    @Transactional
    public String signUpByKakao(KakaoUserResponse userResponse) {
        String email = userResponse.getKakao_account().getEmail();

        String nickname;
        if (userResponse.getKakao_account().getProfile() != null 
                && userResponse.getKakao_account().getProfile().getNickname() != null
                && !userResponse.getKakao_account().getProfile().getNickname().trim().isEmpty()) {
            String originalNickname = userResponse.getKakao_account().getProfile().getNickname().trim();
            nickname = originalNickname.length() > 10 ? originalNickname.substring(0, 10) : originalNickname;
        } else {
            // "카카오" (3글자) + ID 마지막 6자리 = 최대 9글자
            String idSuffix = String.valueOf(userResponse.getId());
            if (idSuffix.length() > 6) {
                idSuffix = idSuffix.substring(idSuffix.length() - 6);
            }
            nickname = "카카오" + idSuffix;
        }

        // 활성 사용자 확인
        Optional<User> activeUser = userRepository.findByEmailAndDeletedAtIsNull(email);
        if (activeUser.isPresent()) {
            return email;
        }

        // 탈퇴된 사용자 확인 및 복구
        Optional<User> deletedUser = userRepository.findByEmailAndDeletedAtIsNotNull(email);
        if (deletedUser.isPresent()) {
            User user = deletedUser.get();
            user.restore();
            
            // 소셜 로그인 정보 업데이트
            if (userResponse.getKakao_account().getProfile() != null) {
                user.updateProfileImageUrl(userResponse.getKakao_account().getProfile().getProfile_image_url());
            }
            
            return user.getEmail();
        }

        // 새로운 사용자 생성
        String dummyPassword = encodePassword("kakao_" + userResponse.getId());
        // 프로필 이미지 업데이트
        String profileImageUrl = userResponse.getKakao_account().getProfile() != null 
            ? userResponse.getKakao_account().getProfile().getProfile_image_url() 
            : null;

        User user = User.registerKakao(email, dummyPassword, userResponse.getId(), nickname, UserRole.GENERAL,
                profileImageUrl);

        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public String signUpByKakao(KakaoUserResponse userResponse, String fcmToken) {
        String email = signUpByKakao(userResponse);
        
        // FCM 토큰이 있으면 업데이트
        if (fcmToken != null && !fcmToken.trim().isEmpty()) {
            User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
            user.updateFcmToken(fcmToken);
        }
        
        return email;
    }

    @Transactional
    public String signUpByNaver(NaverUserResponse userResponse) {
        String email = userResponse.getResponse().getEmail();

        // 활성 사용자 확인
        Optional<User> activeUser = userRepository.findByEmailAndDeletedAtIsNull(email);
        if (activeUser.isPresent()) {
            return email;
        }

        // 탈퇴된 사용자 확인 및 복구
        Optional<User> deletedUser = userRepository.findByEmailAndDeletedAtIsNotNull(email);
        if (deletedUser.isPresent()) {
            User user = deletedUser.get();
            user.restore();
            
            // 소셜 로그인 정보 업데이트
            if (userResponse.getResponse().getProfile_image() != null) {
                user.updateProfileImageUrl(userResponse.getResponse().getProfile_image());
            }
            
            return user.getEmail();
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
        
        User user = User.registerNaver(email, dummyPassword, userResponse.getResponse().getId(),
                naverNickname, UserRole.GENERAL,
                userResponse.getResponse().getProfile_image());

        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public String signUpByNaver(NaverUserResponse userResponse, String fcmToken) {
        String email = signUpByNaver(userResponse);
        
        // FCM 토큰이 있으면 업데이트
        if (fcmToken != null && !fcmToken.trim().isEmpty()) {
            User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
            user.updateFcmToken(fcmToken);
        }
        
        return email;
    }

    @Transactional
    public String signUpByApple(AppleUserResponse userResponse) {
        String appleUserId = userResponse.getSub();
        String providedEmail = userResponse.getEmail();

        // 이메일 숨기기 처리: 시스템 이메일 생성
        String systemEmail;
        if (providedEmail != null && providedEmail.endsWith("@privaterelay.appleid.com")) {
            // 익명 이메일인 경우 시스템 이메일 생성
            systemEmail = "apple_user_" + appleUserId.substring(0, Math.min(appleUserId.length(), 10))
                    + "@kindergarten.system";
        } else if (providedEmail != null) {
            // 실제 이메일인 경우 그대로 사용
            systemEmail = providedEmail;
        } else {
            // 이메일이 없는 경우 시스템 이메일 생성
            systemEmail = "apple_user_" + appleUserId.substring(0, Math.min(appleUserId.length(), 10))
                    + "@kindergarten.system";
        }

        // 활성 사용자 확인
        Optional<User> activeUser = userRepository.findByEmailAndDeletedAtIsNull(systemEmail);
        if (activeUser.isPresent()) {
            return systemEmail;
        }

        // 탈퇴된 사용자 확인 및 복구
        Optional<User> deletedUser = userRepository.findByEmailAndDeletedAtIsNotNull(systemEmail);
        if (deletedUser.isPresent()) {
            User user = deletedUser.get();
            user.restore();
            return user.getEmail();
        }

        // 새로운 사용자 생성
        String dummyPassword = encodePassword("apple_" + appleUserId);
        String nickname;
        if (userResponse.getName() != null && !userResponse.getName().trim().isEmpty()) {
            String originalName = userResponse.getName().trim();
            nickname = originalName.length() > 10 ? originalName.substring(0, 10) : originalName;
        } else {
            // "애플" (2글자) + 사용자 ID 마지막 6자리 = 최대 8글자
            String idSuffix = appleUserId.length() > 6 ? appleUserId.substring(appleUserId.length() - 6) : appleUserId;
            nickname = "애플" + idSuffix;
        }

        User user = User.registerApple(systemEmail, dummyPassword, appleUserId, nickname, UserRole.GENERAL);

        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public String signUpByApple(AppleUserResponse userResponse, String fcmToken) {
        String email = signUpByApple(userResponse);
        
        // FCM 토큰이 있으면 업데이트
        if (fcmToken != null && !fcmToken.trim().isEmpty()) {
            User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                    .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
            user.updateFcmToken(fcmToken);
        }
        
        return email;
    }

    @Transactional
    public void updateHomeShortcut(String email, HomeShortcutsDto homeShortcutsDto) {
        User user = findUser(email);
        user.updateHomeShortcut(homeShortcutsDto.toJson());
    }

    @Transactional
    public void saveCertification(String email, String certification) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCodes.ALREADY_EXIST_EMAIL);
        }

        if (emailCertificationRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCodes.ALREADY_EXIST_EMAIL_CERTIFICATION);
        }

        EmailCertification emailCert = EmailCertification.builder()
                .email(email)
                .code(certification)
                .isCertificated(false)
                .build();

        emailCertificationRepository.save(emailCert);
    }

    @Transactional
    public boolean checkEmailCertification(CheckEmailCertificationRequestDTO request) {
        EmailCertification emailCertification = emailCertificationRepository
                .findByEmail(request.getEmail());

        if (emailCertification == null) {
            throw new BusinessException(ErrorCodes.NOT_FOUND_EMAIL);
        }

        if (emailCertification.getCode().equals(request.getCertification())) {
            emailCertification.completeCertification();
            emailCertificationRepository.save(emailCertification);
            return true;
        } else {
            return false;
        }

    }

    @Transactional
    public void updateUserRole(String email, UpdateUserRoleRequestDTO request) {
        User user = findUser(email);
        user.updateUserRole(request.getRole());
    }

    public void updateTemporaryPassword(String email, String number) {
        User user = findUser(email);
        user.changePassword(passwordEncoder.encode(number));
    }

    public void checkEmailCertificationByTemporaryPassword(String email) {
        EmailCertification emailCertification = emailCertificationRepository
                .findById(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));

        if (!emailCertification.isCertificated()) {
            throw new BusinessException(ErrorCodes.NOT_FOUND_EXCEPTION_BY_TEMPORARY_PASSWORD_EXCEPTION);
        }
    }

    @Transactional(readOnly = true)
    public NotificationSettingsDTO getNotificationSettings(String email) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
        
        return NotificationSettingsDTO.builder()
                .allNotificationsEnabled(user.hasNotificationEnabled(NotificationSetting.ALL_NOTIFICATIONS))
                .communityNotificationsEnabled(user.hasNotificationEnabled(NotificationSetting.COMMUNITY_NOTIFICATIONS))
                .eventNotificationsEnabled(user.hasNotificationEnabled(NotificationSetting.EVENT_NOTIFICATIONS))
                .build();
    }

    @Transactional
    public NotificationSettingsDTO updateNotificationSettings(String email, NotificationSettingsDTO request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));
        
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
    public void markUserAsReviewWriter(String email) {
        User user = getUserByEmail(email);
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
        Page<User> users = userRepository.findUsersWithFilters(
                searchDTO.getEmail(),
                searchDTO.getNickname(),
                searchDTO.getRole(),
                searchDTO.getProvider(),
                searchDTO.getStatus(),
                searchDTO.getKindergartenName(),
                searchDTO.getHasWrittenReview(),
                searchDTO.getIsRestoredUser(),
                pageable
        );
        return users.map(AdminUserResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public AdminUserResponseDTO getUserById(Long userId) {
        User user = userRepository.findByIdWithKindergarten(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));
        return AdminUserResponseDTO.from(user);
    }

    /// 관리자용 - 유저 상태 변경
    @Transactional
    public void updateUserStatus(Long userId, UpdateUserStatusRequestDTO request, String adminEmail) {
        // 관리자 권한 확인
        User admin = getUserByEmail(adminEmail);
        if (!admin.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
        }

        // 대상 유저 조회
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));

        // 관리자는 자신의 상태를 변경할 수 없음
        if (targetUser.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
        }

        // 상태 변경
        targetUser.updateStatus(request.getStatus());
    }

    /// 경력 개월 수 계산
    private int calculateCareerMonths(User user, LocalDate startDate, LocalDate endDate, boolean isAdding) {
        int currentCareerMonths = user.getCareer() == null ? 0 : Integer.parseInt(user.getCareer());
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        return isAdding ?
                currentCareerMonths + (int)monthsBetween :
                currentCareerMonths - (int)monthsBetween;
    }
}
