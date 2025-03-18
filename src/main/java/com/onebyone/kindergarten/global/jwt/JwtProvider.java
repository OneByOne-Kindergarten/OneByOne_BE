package com.onebyone.kindergarten.global.jwt;

import com.onebyone.kindergarten.domain.user.service.CustomUserDetailService;
import com.onebyone.kindergarten.global.jwt.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final CustomUserDetailService customUserDetailService;
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final Long accessTokenValidationMs = 30 * 60 * 1000L;

    private final Long refreshTokenValidationMs = 15 * 24 * 60 * 60 * 1000L;

//    public Long getRefreshTokenValidationMs() { // Redis에 저장 시 사용
//        return refreshTokenValidationMs;
//    }

    public String generateAccessToken(String email) {

        Claims claims = Jwts.claims()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidationMs));

        // Private claim. 서버-클라이언트간의 협의하에 사용되는 클레임.
//        claims.put("email", email);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSignKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(String email) {

        Claims claims = Jwts.claims()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidationMs));

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSignKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey(secretKey))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다. {}", e.toString());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("잘못된 형식의 토큰입니다. {}", e.toString());
            return false;
        } catch (MalformedJwtException e) {
            log.error("잘못된 구조의 토큰입니다. {}", e.toString());
            return false;
        } catch (SignatureException e) {
            log.error("잘못 서명된 토큰입니다. {}", e.toString());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("잘못 생성된 토큰입니다. {}", e.toString());
            return false;
        }
    }

    // JWT payload를 복호화해서 반환
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder() // JwtParserBuilder 인스턴스 생성
                    .setSigningKey(getSignKey(secretKey)) // JWT Signature 검증을 위한 SecretKey 설정
                    .build() // Thread-Safe한 JwtParser를 반환하기 위해 build 호출
                    .parseClaimsJws(token) // Claim(Payload) 파싱
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이어도 refresh token 검증 후 재발급할 수 있또록 claims 반환
            return e.getClaims();
        } catch (Exception e) {
            // 다른 예외인 경우 throw
            log.error("유효하지 않은 토큰입니다. {}", e.toString());
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    // JWT Claims으로 User 객체를 생성하여 Authentication 객체를 반환
    public Authentication getAuthentication(String token) {

        // JWT에서 Claims 가져오기
        Claims claims = getClaims(token);
        String email = claims.getSubject();

        if (email == null) {
            log.error("권한 정보가 없는 토큰입니다. {}", token);
            throw new InvalidTokenException("권한 정보가 없는 토큰입니다.");
        }

        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Long getRemainingTime(String token) {
        Date expiration = getClaims(token).getExpiration();
        Date now = new Date();
        return expiration.getTime() - now.getTime();
    }

}