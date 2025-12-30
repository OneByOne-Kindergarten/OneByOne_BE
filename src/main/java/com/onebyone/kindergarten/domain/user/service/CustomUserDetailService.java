package com.onebyone.kindergarten.domain.user.service;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import com.onebyone.kindergarten.domain.user.repository.UserRepository;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmailAndStatus(email, UserStatus.ACTIVE)
            .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_EMAIL));

    return org.springframework.security.core.userdetails.User.builder()
        .username(email)
        .password(user.getPassword())
        .roles(user.getRole() == UserRole.ADMIN ? "ADMIN" : "USER")
        .build();
  }
}
