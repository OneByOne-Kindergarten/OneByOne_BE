package com.onebyone.kindergarten.domain.user.service;

import com.onebyone.kindergarten.domain.user.dto.ModifyUserNicknameRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.ModifyUserPasswordRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.SignInRequestDTO;
import com.onebyone.kindergarten.domain.user.dto.SignUpRequestDTO;
import com.onebyone.kindergarten.domain.user.entity.User;
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
    public Long signUp(SignUpRequestDTO request) {
        if (isExistedEmail(request.getEmail())) {
            throw new EmailDuplicationException(request.getEmail());
        }

        String encodedPassword = encodePassword(request.getPassword());
        User user = userRepository.save(request.toEntity(encodedPassword));

        return user.getId();
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
}
