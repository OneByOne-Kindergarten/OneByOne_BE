package com.onebyone.kindergarten.global.config;

import com.onebyone.kindergarten.global.jwt.JwtAccessDeniedHandler;
import com.onebyone.kindergarten.global.jwt.JwtEntryPoint;
import com.onebyone.kindergarten.global.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

        // @Bean
        // public CorsConfigurationSource corsConfigurationSource() {
        // CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigins(List.of("http://localhost:3000",
        // "https://one-by-one-fe-git-main-purplenibs-projects.vercel.app"));
        // configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE",
        // "OPTIONS"));
        // configuration.setAllowedHeaders(List.of("*"));
        // configuration.setAllowCredentials(true);

        // UrlBasedCorsConfigurationSource source = new
        // UrlBasedCorsConfigurationSource();
        // source.registerCorsConfiguration("/**", configuration);
        // return source;
        // }

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
                                                                "/kindergarten/*/simple", "/users/email-certification",
                                                                "/users/check-email-certification",
                                                                "/v3/api-docs/**",
                                                                "/community/**",
                                                                "/swagger-resources/**",
                                                                "/webjars/**",
                                                                "/notice/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 전용 API
                                                .anyRequest().authenticated() // 나머지 요청은 인증된 사용자만 접근 가능
                                )

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtEntryPoint) // 인증 실패 시 처리
                                                .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패 시 처리
                                )

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 X
                                )

                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JwtFilter 추가

                return http.build();
        }

}
