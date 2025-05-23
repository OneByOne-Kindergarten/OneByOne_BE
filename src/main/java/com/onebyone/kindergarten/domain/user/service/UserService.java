package com.onebyone.kindergarten.domain.user.service;

import com.onebyone.kindergarten.domain.user.dto.*;
import com.onebyone.kindergarten.domain.user.dto.request.ModifyUserNicknameRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.ModifyUserPasswordRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignInRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.request.SignUpRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.response.KakaoUserResponse;
import com.onebyone.kindergarten.domain.user.dto.response.NaverUserResponse;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.exception.EmailDuplicationException;
import com.onebyone.kindergarten.domain.user.exception.InvalidPasswordException;
import com.onebyone.kindergarten.domain.user.exception.NotFoundEmailException;
import com.onebyone.kindergarten.domain.user.exception.PasswordMismatchException;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public String signUp(SignUpRequestDTO request) {
        if (isExistedEmail(request.getEmail())) {
            throw new EmailDuplicationException(request.getEmail());
        }

        String encodedPassword = encodePassword(request.getPassword());
        User user = userRepository.save(request.toEntity(encodedPassword));

        return user.getEmail();
    }

    @Transactional(readOnly = true)
    public String findById(Long id) throws AccountNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("id가 존재하지 않습니다."));

        return user.getEmail();
    }

    @Transactional(readOnly = true)
    public boolean isExistedEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Transactional
    public String signIn(SignInRequestDTO request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new NotFoundEmailException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        if (request.getFcmToken() != null) {
            user.updateFcmToken(request.getFcmToken());
        }

        return user.getEmail();
    }

    @Transactional
    public void changeNickname(String email, ModifyUserNicknameRequestDTO request) {
        User user = findUser(email);
        user.changeNickname(request.getNewNickname());
    }

    @Transactional
    public void changePassword(String email, ModifyUserPasswordRequestDTO request) {
        User user = findUser(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private User findUser(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new NotFoundEmailException("이메일이 존재하지 않습니다"));
    }

    @Transactional
    public void withdraw(String email) {
        User user = findUser(email);
        user.withdraw();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new NotFoundEmailException("이메일이 존재하지 않습니다"));
    }

    @Transactional
    public void updateCareer(User user, String career) {
        user.updateCareer(career);
    }

    public UserDTO getUser(String email) {
        return UserDTO.from(userRepository.findUserWithKindergarten(email).orElseThrow(() -> new NotFoundEmailException("이메일이 존재하지 않습니다")));
    }

    @Transactional
    public String signUpByKakao(KakaoUserResponse userResponse) {
        String email = userResponse.getKakao_account().getEmail();

        String nickname = userResponse.getKakao_account().getProfile() != null ?
                userResponse.getKakao_account().getProfile().getNickname() : "카카오_" + userResponse.getId();

        if (isExistedEmail(email)) {
            return email;
        }

        String dummyPassword = encodePassword("kakao_" + userResponse.getId());

        User user = User.registerKakao(email, dummyPassword, userResponse.getId(), nickname, UserRole.GENERAL, userResponse.getKakao_account().getProfile().getProfile_image_url());

        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public String signUpByNaver(NaverUserResponse userResponse) {
        String email = userResponse.getResponse().getEmail();

        if (isExistedEmail(email)) {
            return email;
        }

        String dummyPassword = encodePassword("kakao_" + userResponse.getResponse().getId());

        User user = User.registerNaver(email, dummyPassword, userResponse.getResponse().getId(), userResponse.getResponse().getNickname(), UserRole.GENERAL, userResponse.getResponse().getProfile_image());

        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public void updateHomeShortcut(String email, HomeShortcutsDto homeShortcutsDto) {
        User user = findUser(email);
        user.updateHomeShortcut(homeShortcutsDto.toJson());
    }

    /// 관리자 로그인
    public String signInAdmin(String email, String password) {
        // 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new NotFoundEmailException("이메일이 존재하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 발급
        return user.getEmail();
    }
}
