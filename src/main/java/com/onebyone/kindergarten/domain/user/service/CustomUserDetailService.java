package com.onebyone.kindergarten.domain.user.service;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.exception.NotFoundEmailException;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundEmailException("이메일이 존재하지 않습니다."));

        return org.springframework.security.core.userdetails.User.builder()
                .username(email)
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
