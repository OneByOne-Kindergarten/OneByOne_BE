package com.onebyone.kindergarten.global.jwt;

import com.onebyone.kindergarten.domain.user.service.CustomUserDetailService;
import com.onebyone.kindergarten.global.exception.*;
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

    public String generateAccessToken(String email) {

        Claims claims = Jwts.claims()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidationMs));

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
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_MALFORMED);
        } catch (SignatureException e) {
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_SIGNATURE);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_ILLEGAL);
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
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.toString());
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_ILLEGAL);
        }
    }

    // JWT Claims으로 User 객체를 생성하여 Authentication 객체를 반환
    public Authentication getAuthentication(String token) {

        // JWT에서 Claims 가져오기
        Claims claims = getClaims(token);
        String email = claims.getSubject();

        if (email == null) {
            log.error("권한 정보가 없는 토큰입니다. {}", token);
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_ILLEGAL);
        }

        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmailFromRefreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_ILLEGAL);
        }

        return getClaims(refreshToken).getSubject(); // email
    }
}