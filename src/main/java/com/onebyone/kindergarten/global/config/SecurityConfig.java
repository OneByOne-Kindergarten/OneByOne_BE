package com.onebyone.kindergarten.global.config;

import com.onebyone.kindergarten.global.jwt.JwtEntryPoint;
import com.onebyone.kindergarten.global.jwt.JwtFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtEntryPoint jwtEntryPoint;
  private final JwtFilter jwtFilter;

  private final List<String> permitOriginList =
      new ArrayList<>(
          Arrays.asList(
              "/users/sign-up",
              "/users/sign-in",
              "/users/reissue",
              "/users/email-certification",
              "/users/temporary-password",
              "/users/check-email-certification",
              "/users/kakao/callback",
              "/users/naver/callback",
              "/users/apple/callback",
              "/swagger-ui/**",
              "/v3/api-docs/**",
              "/swagger-resources/**",
              "/webjars/**",
              "/kindergarten/*/simple",
              "/address",
              "/community/**",
              "/notice/**"));

  private final List<String> adminOriginList =
      new ArrayList<>(Arrays.asList("/admin/**", "/address/batch/**"));

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable()) // Non-Browser Clients만을 위한 API 서버이므로, CSRF 보호 기능 해제
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // h2-console 사용을
        // 위한 설정

        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(CorsUtils::isPreFlightRequest)
                    .permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(permitOriginList.toArray(new String[0]))
                    .permitAll()
                    .requestMatchers(adminOriginList.toArray(new String[0]))
                    .hasRole("ADMIN") // 관리자 전용 API
                    .anyRequest()
                    .authenticated() // 나머지 요청은 인증된 사용자만 접근 가능
            )
        .exceptionHandling(
            ex -> ex.authenticationEntryPoint(jwtEntryPoint) // 인증 실패 시 처리
            )
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 X
            )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JwtFilter 추가

    return http.build();
  }
}
