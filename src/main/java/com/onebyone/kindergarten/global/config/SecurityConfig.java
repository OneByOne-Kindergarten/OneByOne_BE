package com.onebyone.kindergarten.global.config;

import com.onebyone.kindergarten.global.jwt.JwtAccessDeniedHandler;
import com.onebyone.kindergarten.global.jwt.JwtEntryPoint;
import com.onebyone.kindergarten.global.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtEntryPoint jwtEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Non-Browser Clients만을 위한 API 서버이므로, CSRF 보호 기능 해제
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // h2-console 사용을 위한 설정

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/h2-console/**", "/users/sign-up", "/users/sign-in","/swagger-ui/**", 
                                "/users/reissue", "/users/kakao/callback", "/users/naver/callback", 
                                "/kindergarten/*/simple",
                                "/v3/api-docs/**",
                                "/community/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/notice/**",
                                "/admin/css/**",
                                "/admin/js/**",
                                "/admin/images/**",
                                "/favicon.ico",
                                "/static/**",
                                "/admin/login",
                                "/admin/login/**").permitAll()
                        // 관리자 API는 ADMIN 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 나머지 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtEntryPoint) // 인증 실패 시 처리
                        .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패 시 처리
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 관리 정책 설정
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JwtFilter 추가

        return http.build();
    }

}
