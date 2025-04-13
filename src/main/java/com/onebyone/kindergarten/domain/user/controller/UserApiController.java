package com.onebyone.kindergarten.domain.user.controller;

import com.onebyone.kindergarten.domain.facade.UserFacade;
import com.onebyone.kindergarten.domain.user.dto.request.*;
import com.onebyone.kindergarten.domain.user.dto.response.GetUserResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.response.ReIssueResponseDTO;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.jwt.JwtProvider;
import com.onebyone.kindergarten.global.jwt.exception.InvalidTokenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@Tag(name = "User API", description = "유저 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserFacade userFacade;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "유저-01 회원가입", description = "계정 생성합니다.")
    @PostMapping("/sign-up")
    public void signUp(
            @RequestBody @Valid final SignUpRequestDTO request
    ) throws AccountNotFoundException {
        userFacade.signUp(request);
    }

    @Operation(summary = "유저-02 로그인", description = "로그인 입니다")
    @PostMapping("/sign-in")
    public SignInResponseDTO signIn(
            @RequestBody SignInRequestDTO request
    ) {
        return userFacade.signIn(request);
    }

    @Operation(summary = "유저-03 비밀번호 변경", description = "비밀번호 변경입니다.")
    @PatchMapping("/password")
    public void changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ModifyUserPasswordRequestDTO request
    ) {
        userService.changePassword(userDetails.getUsername(), request);
    }

    @Operation(summary = "유저-04 닉네임 변경", description = "닉네임 변경입니다.")
    @PatchMapping("/nickname")
    public void changeNickname(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ModifyUserNicknameRequestDTO request
    ) {
        userService.changeNickname(userDetails.getUsername(), request);
    }

    @Operation(summary = "유저-05 회원탈퇴", description = "회원 탈퇴입니다.")
    @PostMapping("/withdraw")
    public void withdraw(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.withdraw(userDetails.getUsername());
    }

    @Operation(summary = "유저-06 유저정보", description = "유저 조회입니다.")
    @GetMapping
    public GetUserResponseDTO getUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new GetUserResponseDTO(userService.getUser(userDetails.getUsername()));
    }

    @Operation(summary = "유저-07 토큰 재발급", description = "토큰 재발급입니다.")
    @PostMapping("/reissue")
    public ReIssueResponseDTO reissue(
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("RefreshToken이 존재하지 않거나 형식이 잘못되었습니다.");
        }

        String refreshToken = authHeader.substring(7);

        // ✅ refreshToken에서 email 추출 및 검증
        String email = jwtProvider.getEmailFromRefreshToken(refreshToken);

        // ✅ 새 토큰 발급
        String newAccessToken = jwtProvider.generateAccessToken(email);
        String newRefreshToken = jwtProvider.generateRefreshToken(email);

        return ReIssueResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

//  TODO: 방식 협의 됐을 때
//    @Operation(summary = "이메일 찾기", description = "이메일 찾기")
//    @PostMapping("/email")
//    public void changeEmail(){
//
//    }

//  TODO: 방식 협의 됐을 때
//    @Operation(summary = "바밀번호 재설정", description = "비밀번호 재설정")
//    @PostMapping("/email")
//    public void changeEmail(){
//
//    }


}
