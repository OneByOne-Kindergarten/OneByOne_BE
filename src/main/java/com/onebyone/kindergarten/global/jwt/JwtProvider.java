package com.onebyone.kindergarten.global.jwt;

import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.user.service.CustomUserDetailService;
import com.onebyone.kindergarten.global.exception.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final CustomUserDetailService customUserDetailService;
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final Long accessTokenValidationMs = 30 * 60 * 1000L;
    private final Long refreshTokenValidationMs = 15 * 24 * 60 * 60 * 1000L;

    public String generateAccessToken(Long userId, UserRole role) {

        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidationMs));

        claims.put("role", String.valueOf(role));

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSignKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(Long userId, UserRole role) {

        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidationMs));

        claims.put("role", String.valueOf(role));

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
        } catch (JwtException e) {
            return false;
        }
    }

    public Map<String, Object> validateTokenWithError(String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey(secretKey))
                    .build()
                    .parseClaimsJws(token);
            result.put("isValid", true);
        } catch (ExpiredJwtException e) {
            result.put("isValid", false);
            result.put("errorCode", ErrorCodes.INVALID_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            result.put("isValid", false);
            result.put("errorCode", ErrorCodes.INVALID_TOKEN_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            result.put("isValid", false);
            result.put("errorCode", ErrorCodes.INVALID_TOKEN_MALFORMED);
        } catch (IllegalArgumentException e) {
            result.put("isValid", false);
            result.put("errorCode", ErrorCodes.INVALID_TOKEN_ILLEGAL);
        } catch (Exception e) {
            result.put("isValid", false);
            result.put("errorCode", ErrorCodes.INTERNAL_SERVER_ERROR);
        }

        return result;
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
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_ILLEGAL);
        }
    }

    // JWT Claims으로 User 객체를 생성하여 Authentication 객체를 반환
    public Map<String, Object> getAuthentication(String token) {
        Map<String, Object> result = new HashMap<>();

        // JWT에서 Claims 가져오기
        Claims claims = getClaims(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);

        if (userId == null || role == null) {
            result.put("isValid", false);
            result.put("errorCode", ErrorCodes.INVALID_TOKEN_ILLEGAL);
            return result;        }

        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

        UserDetails userDetails = User
                .withUsername(userId)
                .password("")
                .authorities(grantedAuthorities)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        result.put("isValid", true);
        result.put("authentication", authentication);
        return result;
    }

    public Claims getClaimFromRefreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new BusinessException(ErrorCodes.INVALID_TOKEN_ILLEGAL);
        }

        return getClaims(refreshToken);
    }
}