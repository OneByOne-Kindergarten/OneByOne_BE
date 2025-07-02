package com.onebyone.kindergarten.domain.user.controller;

import com.onebyone.kindergarten.domain.communityComments.dto.response.PageCommunityCommentsResponseDTO;
import com.onebyone.kindergarten.domain.facade.UserFacade;
import com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto.InternshipReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.kindergartenWorkReview.dto.WorkReviewPagedResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.HomeShortcutsDto;
import com.onebyone.kindergarten.domain.user.dto.request.*;
import com.onebyone.kindergarten.domain.user.dto.response.GetUserResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.response.ReIssueResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.response.SignInResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.response.SignUpResponseDTO;
import com.onebyone.kindergarten.domain.user.dto.response.UpdateHomeShortcutsResponseDTO;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.jwt.JwtProvider;
import com.onebyone.kindergarten.global.jwt.exception.InvalidTokenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    public SignUpResponseDTO signUp(
            @RequestBody @Valid final SignUpRequestDTO request) {
        return userFacade.signUp(request);
    }

    @Operation(summary = "유저-02 로그인", description = "로그인 입니다")
    @PostMapping("/sign-in")
    public SignInResponseDTO signIn(
            @RequestBody SignInRequestDTO request) {
        return userFacade.signIn(request);
    }

    @Operation(summary = "유저-03 비밀번호 변경", description = "비밀번호 변경입니다.")
    @PatchMapping("/password")
    public void changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ModifyUserPasswordRequestDTO request) {
        userService.changePassword(userDetails.getUsername(), request);
    }

    @Operation(summary = "유저-04 닉네임 변경", description = "닉네임 변경입니다.")
    @PatchMapping("/nickname")
    public void changeNickname(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ModifyUserNicknameRequestDTO request) {
        userService.changeNickname(userDetails.getUsername(), request);
    }

    @Operation(summary = "유저-05 회원탈퇴", description = "회원 탈퇴입니다.")
    @PostMapping("/withdraw")
    public void withdraw(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.withdraw(userDetails.getUsername());
    }

    @Operation(summary = "유저-06 유저정보", description = "유저 조회입니다.")
    @GetMapping
    public GetUserResponseDTO getUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        return new GetUserResponseDTO(userService.getUser(userDetails.getUsername()));
    }

    @Operation(summary = "유저-07 토큰 재발급", description = "토큰 재발급입니다.")
    @PostMapping("/reissue")
    public ReIssueResponseDTO reissue(
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("RefreshToken이 존재하지 않거나 형식이 잘못되었습니다.");
        }

        String refreshToken = authHeader.substring(7);

        String email = jwtProvider.getEmailFromRefreshToken(refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(email);
        String newRefreshToken = jwtProvider.generateRefreshToken(email);

        return ReIssueResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Operation(summary = "유저-08 카카오 소셜 로그인", description = "카카오 소셜로그인을 진행합니다")
    @GetMapping("/kakao/callback")
    public SignInResponseDTO getKakaoAuthorizationCode(
            @RequestParam(name = "code") String code) {
        return userFacade.kakaoLogin(code);
    }

    @Operation(summary = "유저-09 네이버 소셜 로그인", description = "네이버 소셜로그인을 진행합니다")
    @GetMapping("/naver/callback")
    public SignInResponseDTO getNaverAuthorizationCode(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "state") String state) {
        return userFacade.naverLogin(code, state);
    }

    @Operation(summary = "유저-10 애플 소셜 로그인", description = "애플 소셜로그인을 진행합니다")
    @PostMapping("/apple/callback")
    public void appleCallback(
            @RequestParam("id_token") String idToken,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response) throws IOException {

        try {
            /// 애플 로그인 처리
            SignInResponseDTO loginResponse = userFacade.appleLogin(idToken);

            /// 프론트엔드로 리다이렉트 (성공)
            String frontendUrl = "https://one-by-one-fe.vercel.app/users/apple/callback";
            String redirectUrl = String.format("%s?access_token=%s&refresh_token=%s",
                    frontendUrl,
                    loginResponse.getAccessToken(),
                    loginResponse.getRefreshToken());

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            /// 프론트엔드로 리다이렉트 (에러)
            String frontendUrl = "https://one-by-one-fe.vercel.app/users/apple/callback";
            String redirectUrl = String.format("%s?error=login_failed&message=%s",
                    frontendUrl,
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));

            response.sendRedirect(redirectUrl);
        }
    }

    @Operation(summary = "유저-010 작성한 댓글 조회", description = "작성한 댓글을 조회합니다.")
    @GetMapping("/user/community-comments")
    public PageCommunityCommentsResponseDTO getWroteMyCommunityComments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userFacade.getWroteMyCommunityComments(userDetails.getUsername(), page, size);
    }

    @Operation(summary = "유저-11 홈 바로가기 정보 업데이트", description = "사용자의 홈 바로가기 정보를 업데이트합니다.")
    @PutMapping("/shortcuts")
    public UpdateHomeShortcutsResponseDTO updateHomeShortcut(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody HomeShortcutsDto request) {
        userService.updateHomeShortcut(userDetails.getUsername(), request);
        return UpdateHomeShortcutsResponseDTO.success();
    }

    @Operation(summary = "유저-12 이메일 인증 번호 발송", description = "인증번호를 발송합니다.")
    @PostMapping("/email-certification")
    public ResponseEntity<String> emailCertification(
            @RequestBody EmailCertificationRequestDTO request) {
        boolean isSended = userFacade.emailCertification(request.getEmail());

        if (isSended) {
            return ResponseEntity.ok("인증번호가 성공적으로 발송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("인증번호 발송에 실패했습니다.");
        }
    }

    @Operation(summary = "유저-13 이메일 인증 번호 검증", description = "인증번호를 검증합니다.")
    @PostMapping("/check-email-certification")
    public ResponseEntity<String> checkEmailCertification(
            @RequestBody CheckEmailCertificationRequestDTO request) {
        boolean isChecked = userService.checkEmailCertification(request);

        if (isChecked) {
            return ResponseEntity.ok("이메일 인증에 성공했습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                    .body("이메일 인증에 실패했습니다.");
        }
    }

    @Operation(summary = "유저-14 유저 역할 변경", description = "사용자의 역할(교사, 예비교사) 수정합니다.")
    @PostMapping("/role")
    public ResponseEntity<String> updateUserRole(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserRoleRequestDTO request) {
        userService.updateUserRole(userDetails.getUsername(), request);
        return ResponseEntity.ok("권한이 변경되었습니다.");
    }

    @Operation(summary = "유저-15 비밀번호 재설정", description = "유저의 비밀번호를 임시 비밀번호로 변경합니다.")
    @PatchMapping("/temporary-password")
    public ResponseEntity<String> updateTemporaryPassword(
            @RequestBody UpdateTemporaryPasswordRequestDTO request) {
        boolean isSended = userFacade.updateTemporaryPassword(request.getEmail());

        if (isSended) {
            return ResponseEntity.ok("임시 비밀번호 변경이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                    .body("임시 비밀번호 변경이 실패했습니다.");
        }
    }

    @Operation(summary = "유저-016 내가 작성한 실습 리뷰 조회", description = "내가 작성한 실습 리뷰를 조회합니다.")
    @GetMapping("/user/internship-reviews")
    public InternshipReviewPagedResponseDTO getMyInternshipReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userFacade.getMyInternshipReviews(userDetails.getUsername(), page, size);
    }

    @Operation(summary = "유저-017 내가 작성한 근무 리뷰 조회", description = "내가 작성한 근무 리뷰를 조회합니다.")
    @GetMapping("/user/work-reviews")
    public WorkReviewPagedResponseDTO getMyWorkReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userFacade.getMyWorkReviews(userDetails.getUsername(), page, size);
    }
}
