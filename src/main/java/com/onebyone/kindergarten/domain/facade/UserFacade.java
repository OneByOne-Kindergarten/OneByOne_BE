package com.onebyone.kindergarten.domain.facade;

import com.onebyone.kindergarten.domain.communityComments.dto.response.PageCommunityCommentsResponseDTO;
import com.onebyone.kindergarten.domain.communityComments.service.CommunityCommentService;
import com.onebyone.kindergarten.domain.feignClient.KakaoApiClient;
import com.onebyone.kindergarten.domain.feignClient.KakaoAuthClient;
import com.onebyone.kindergarten.domain.feignClient.NaverApiClient;
import com.onebyone.kindergarten.domain.feignClient.NaverAuthClient;
import com.onebyone.kindergarten.domain.provider.EmailProvider;
import com.onebyone.kindergarten.domain.user.dto.UserDTO;
import com.onebyone.kindergarten.domain.user.dto.response.AppleUserResponse;
import com.onebyone.kindergarten.domain.user.service.AppleAuthService;
import com.onebyone.kindergarten.domain.user.dto.response.*;
import com.onebyone.kindergarten.domain.user.dto.request.SignInRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignUpRequestDTO;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacade {
    private final UserService userService;
    private final CommunityCommentService communityCommentService;
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
        String email = userService.signUp(request);

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return SignUpResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public SignInResponseDTO signIn(SignInRequestDTO request) {
        String email = userService.signIn(request);

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return SignInResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public SignInResponseDTO kakaoLogin(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoApiKey);
        params.add("redirect_uri", kakaoRedirectUrl);
        params.add("code", code);

        log.info("=== 카카오 토큰 요청 파라미터 ===");
        log.info("client_id: {}", kakaoApiKey);
        log.info("redirect_uri: {}", kakaoRedirectUrl);
        log.info("code: {}", code);
        log.info("============================");

        KakaoTokenResponse tokenResponse = kakaoAuthClient.getAccessToken(params);
        String kakaoAccessToken = tokenResponse.getAccess_token();

        KakaoUserResponse userResponse = kakaoApiClient.getUserInfo("Bearer " + kakaoAccessToken);
        String email = userService.signUpByKakao(userResponse);

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return SignInResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public SignInResponseDTO naverLogin(String code, String state) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", code);
        params.add("state", state);

        NaverTokenResponse response = naverAuthClient.getAccessToken(params);

        NaverUserResponse userResponse = naverApiClient.getUserInfo("Bearer " + response.getAccess_token());
        String email = userService.signUpByNaver(userResponse);

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return SignInResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public SignInResponseDTO appleLogin(String idToken) {
        // Apple ID Token 검증 및 사용자 정보 추출
        AppleUserResponse userResponse = appleAuthService.verifyIdToken(idToken);
        String email = userService.signUpByApple(userResponse);

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        return SignInResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public PageCommunityCommentsResponseDTO getWroteMyCommunityComments(String username, int page, int size) {
        UserDTO user = userService.getUser(username);
        return communityCommentService.getWroteMyCommunityComments(user.getUserId(), page, size);
    }

    @Transactional
    public boolean emailCertification(String email) {
        String number = createNumber();
        boolean isSaved = userService.saveCertification(email, number);
        if (isSaved) {
            boolean isSended = emailProvider.sendCertifivationMail(email, number);
            if (isSended) {
                return true;
            }
        }
        return false;
    }

    public String createNumber() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

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
