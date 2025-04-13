package com.onebyone.kindergarten.domain.facade;

import com.onebyone.kindergarten.domain.user.dto.request.SignInRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignInResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignUpRequestDTO;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountNotFoundException;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    public void signUp(SignUpRequestDTO request) throws AccountNotFoundException {
        Long userId = userService.signUp(request);
        String email = userService.findById(userId);
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

}
