package com.onebyone.kindergarten.domain.facade;

import com.onebyone.kindergarten.domain.feignClient.KakaoApiClient;
import com.onebyone.kindergarten.domain.feignClient.KakaoAuthClient;
import com.onebyone.kindergarten.domain.user.dto.response.KakaoTokenResponse;
import com.onebyone.kindergarten.domain.user.dto.response.KakaoUserResponse;
import com.onebyone.kindergarten.domain.user.dto.request.SignInRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.response.SignInResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignUpRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.response.SignUpResponseDTO;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.security.auth.login.AccountNotFoundException;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final KakaoApiClient kakaoApiClient;
    private final KakaoAuthClient kakaoAuthClient;
    @Value("${oauth.secret-key}")
    private String apiKey;
    @Value("${oauth.url.redirect-uri}")
    private String redirectUrl;

    public SignUpResponseDTO signUp(SignUpRequestDTO request){
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
        params.add("client_id", apiKey);
        params.add("redirect_uri", redirectUrl);
        params.add("code", code);

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
}
