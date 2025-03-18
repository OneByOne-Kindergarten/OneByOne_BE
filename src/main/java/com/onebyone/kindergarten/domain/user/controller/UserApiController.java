package com.onebyone.kindergarten.domain.user.controller;

import com.onebyone.kindergarten.domain.facade.UserFacade;
import com.onebyone.kindergarten.domain.user.dto.SignInRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.SignInResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.SignUpRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

@Tag(name = "User API", description = "유저 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserFacade userFacade;

    @Operation(summary = "회원가입", description = "계정 생성합니다.")
    @PostMapping("/sign-up")
    public void signUp(
            @RequestBody @Valid final SignUpRequestDTO request
    ) throws AccountNotFoundException {
        userFacade.signUp(request);
    }

    @Operation(summary = "로그인", description = "로그인 입니다")
    @PostMapping("/sign-in")
    public SignInResponseDTO signIn(
            @RequestBody SignInRequestDTO request
    ) {
        return userFacade.signIn(request);
    }
}
