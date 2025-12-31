package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.communityComments.dto.response.PageCommunityCommentsResponseDTO;
import com.onebyone.kindergarten.domain.communityComments.service.CommunityCommentService;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.service.KindergartenInternshipReviewService;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.service.KindergartenWorkReviewService;
import com.onebyone.kindergarten.domain.user.dto.JwtUserInfoDto;
import com.onebyone.kindergarten.domain.user.dto.UserDTO;
import com.onebyone.kindergarten.domain.user.dto.request.EmailCertificationRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignInRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignUpRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.UpdateTemporaryPasswordRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.response.*;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.EmailCertificationType;
import com.onebyone.kindergarten.domain.user.service.AppleAuthService;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.feignClient.KakaoApiClient;
import com.onebyone.kindergarten.global.feignClient.KakaoAuthClient;
import com.onebyone.kindergarten.global.feignClient.NaverApiClient;
import com.onebyone.kindergarten.global.feignClient.NaverAuthClient;
import com.onebyone.kindergarten.global.jwt.JwtProvider;
import com.onebyone.kindergarten.global.provider.EmailProvider;
import jakarta.transaction.Transactional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacade {
  private final UserService userService;
  private final CommunityCommentService communityCommentService;
  private final KindergartenInternshipReviewService kindergartenInternshipReviewService;
  private final KindergartenWorkReviewService kindergartenWorkReviewService;
  private final JwtProvider jwtProvider;
  private final KakaoApiClient kakaoApiClient;
  private final KakaoAuthClient kakaoAuthClient;
  private final NaverAuthClient naverAuthClient;
  private final NaverApiClient naverApiClient;
  private final AppleAuthService appleAuthService;
  private final EmailProvider emailProvider;

  @Value("${oauth.kakao.secret-key}")
  private String kakaoApiKey;

  @Value("${oauth.kakao.url.redirect-uri}")
  private String kakaoRedirectUrl;

  @Value("${oauth.naver.client-id}")
  private String naverClientId;

  @Value("${oauth.naver.client-secret}")
  private String naverClientSecret;

  public SignUpResponseDTO signUp(SignUpRequestDTO request) {
    JwtUserInfoDto dto = userService.signUp(request);

    String accessToken = jwtProvider.generateAccessToken(dto.getUserId(), dto.getRole());
    String refreshToken = jwtProvider.generateRefreshToken(dto.getUserId(), dto.getRole());

    return SignUpResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public SignInResponseDTO signIn(SignInRequestDTO request) {
    JwtUserInfoDto dto = userService.signIn(request);

    String accessToken = jwtProvider.generateAccessToken(dto.getUserId(), dto.getRole());
    String refreshToken = jwtProvider.generateRefreshToken(dto.getUserId(), dto.getRole());

    return SignInResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  @Transactional
  public SignInResponseDTO kakaoLogin(String code, String fcmToken) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoApiKey);
    params.add("redirect_uri", kakaoRedirectUrl);
    params.add("code", code);

    log.info("=== 카카오 토큰 요청 파라미터 ===");
    log.info("client_id: {}", kakaoApiKey);
    log.info("redirect_uri: {}", kakaoRedirectUrl);
    log.info("code: {}", code);
    log.info("fcmToken 존재: {}", fcmToken != null);
    log.info("============================");

    KakaoTokenResponse tokenResponse = kakaoAuthClient.getAccessToken(params);
    String kakaoAccessToken = tokenResponse.getAccess_token();

    KakaoUserResponse userResponse = kakaoApiClient.getUserInfo("Bearer " + kakaoAccessToken);

    User user = userService.signUpByKakao(userResponse, fcmToken);

    String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
    String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole());

    return SignInResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  @Transactional
  public SignInResponseDTO naverLogin(String code, String state, String fcmToken) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", naverClientId);
    params.add("client_secret", naverClientSecret);
    params.add("code", code);
    params.add("state", state);

    log.info("=== 네이버 토큰 요청 파라미터 ===");
    log.info("client_id: {}", naverClientId);
    log.info("code: {}", code);
    log.info("state: {}", state);
    log.info("fcmToken 존재: {}", fcmToken != null);
    log.info("============================");

    NaverTokenResponse response = naverAuthClient.getAccessToken(params);

    NaverUserResponse userResponse =
        naverApiClient.getUserInfo("Bearer " + response.getAccess_token());
    User user = userService.signUpByNaver(userResponse);

    if (fcmToken != null && !fcmToken.trim().isEmpty()) {
      user.updateFcmToken(fcmToken);
    }

    String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
    String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole());

    return SignInResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public SignInResponseDTO appleLogin(String idToken, String fcmToken) {

    log.info("=== 애플 로그인 요청 ===");
    log.info("idToken 존재: {}", idToken != null);
    log.info("fcmToken 존재: {}", fcmToken != null);
    log.info("=====================");

    // Apple ID Token 검증 및 사용자 정보 추출
    AppleUserResponse userResponse = appleAuthService.verifyIdToken(idToken);
    User user = userService.signUpByApple(userResponse);

    if (fcmToken != null && !fcmToken.trim().isEmpty()) {
      user.updateFcmToken(fcmToken);
    }

    String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
    String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole());

    return SignInResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public PageCommunityCommentsResponseDTO getWroteMyCommunityComments(
      Long userId, int page, int size) {
    UserDTO user = userService.getUserToDTO(userId);
    return communityCommentService.getWroteMyCommunityComments(user.getUserId(), page, size);
  }

  /// 내가 작성한 실습 리뷰 조회
  public InternshipReviewPagedResponseDTO getMyInternshipReviews(Long userId, int page, int size) {
    return kindergartenInternshipReviewService.getMyReviews(userId, page, size);
  }

  /// 내가 작성한 근무 리뷰 조회
  public WorkReviewPagedResponseDTO getMyWorkReviews(Long userId, int page, int size) {
    AdminUserResponseDTO user = userService.getUserToAdminDTO(userId);
    return kindergartenWorkReviewService.getMyReviews(user.getId(), page, size);
  }

  @Transactional
  public boolean emailCertification(EmailCertificationRequestDTO request) {
    String code = createNumber();

    EmailCertificationType certificationType = request.getCertificationType();
    if (EmailCertificationType.EMAIL.equals(certificationType)) {
      userService.saveSignUpCertification(request, code);
    } else if (EmailCertificationType.TEMPORARY_PASSWORD.equals(certificationType)) {
      userService.savePasswordCertification(request, code);
    } else {
      userService.saveSignUpCertification(request, code);
    }

    return emailProvider.sendCertificationMail(request.getEmail(), code);
  }

  @Transactional
  public boolean updateTemporaryPasswordCertification(UpdateTemporaryPasswordRequestDTO request) {
    String number = createNumber();
    userService.checkEmailCertificationByTemporaryPassword(request.getEmail(), request.getCode());
    userService.updateTemporaryPassword(request.getEmail(), number);
    return emailProvider.sendTemporaryPasswordMail(request.getEmail(), number);
  }

  public String createNumber() {
    Random random = new Random();
    StringBuilder key = new StringBuilder();

    for (int i = 0; i < 8; i++) {
      int idx = random.nextInt(3);

      switch (idx) {
        case 0:
          key.append((char) (random.nextInt(26) + 97));
          break;
        case 1:
          key.append((char) (random.nextInt(26) + 65));
          break;
        case 2:
          key.append(random.nextInt(9));
          break;
      }
    }
    return key.toString();
  }
}
