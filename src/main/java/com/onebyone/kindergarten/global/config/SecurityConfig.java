package com.onebyone.kindergarten.global.config;

import com.onebyone.kindergarten.global.jwt.JwtEntryPoint;
import com.onebyone.kindergarten.global.jwt.JwtFilter;
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

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers("/", "/h2-console/**", "/users/sign-up",
                                                                "/users/sign-in", "/swagger-ui/**", "/users/reissue",
                                                                "/users/kakao/callback", "/users/naver/callback",
                                                                "/users/apple/callback",
                                                                "/kindergarten/*/simple", "/users/email-certification",
                                                                "/users/temporary-password",
                                                                "/users/check-email-certification",
                                                                "/v3/api-docs/**",
                                                                "/address",
                                                                "/community/**",
                                                                "/swagger-resources/**",
                                                                "/sample/v2/sample",
                                                                "/webjars/**",
                                                                "/notice/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 전용 API
                                                .anyRequest().authenticated() // 나머지 요청은 인증된 사용자만 접근 가능
                                )
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtEntryPoint) // 인증 실패 시 처리
                                )

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 X
                                )

                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JwtFilter 추가

                return http.build();
        }

}
