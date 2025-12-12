package com.onebyone.kindergarten.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onebyone.kindergarten.domain.user.dto.response.ApplePublicKeyResponse;
import com.onebyone.kindergarten.domain.user.dto.response.AppleUserResponse;
import com.onebyone.kindergarten.global.feignClient.AppleAuthClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleAuthService {

  private final AppleAuthClient appleAuthClient;
  private final ObjectMapper objectMapper;

  @Value("${oauth.apple.team-id}")
  private String teamId;

  @Value("${oauth.apple.client-id}")
  private String clientId;

  @Value("${oauth.apple.key-id}")
  private String keyId;

  @Value("${oauth.apple.private-key}")
  private String privateKey;

  @Value("${oauth.apple.audience:https://appleid.apple.com}")
  private String audience;

  public AppleUserResponse verifyIdToken(String idToken) {
    try {
      // 1. JWT 헤더에서 kid 추출
      String[] tokenParts = idToken.split("\\.");
      String header = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
      String kid = objectMapper.readTree(header).get("kid").asText();

      // 2. Apple 공개키 조회
      ApplePublicKeyResponse publicKeys = appleAuthClient.getPublicKeys();
      ApplePublicKeyResponse.Key appleKey =
          publicKeys.getKeys().stream()
              .filter(key -> key.getKid().equals(kid))
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Apple 공개키를 찾을 수 없습니다."));

      // 3. 공개키로 JWT 검증
      PublicKey publicKey = generatePublicKey(appleKey.getN(), appleKey.getE());
      Claims claims =
          Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(idToken).getBody();

      // 4. 사용자 정보 추출
      AppleUserResponse userResponse = new AppleUserResponse();
      userResponse.setSub(claims.getSubject());
      userResponse.setEmail(claims.get("email", String.class));
      userResponse.setEmail_verified(claims.get("email_verified", Boolean.class));
      userResponse.setIs_private_email(claims.get("is_private_email", Boolean.class));

      // name은 첫 로그인 시에만 제공되므로 null일 수 있음
      Object nameObj = claims.get("name");
      if (nameObj != null) {
        userResponse.setName(nameObj.toString());
      }

      return userResponse;

    } catch (Exception e) {
      log.error("Apple ID Token 검증 실패: {}", e.getMessage());
      throw new RuntimeException("Apple 로그인 검증에 실패했습니다.", e);
    }
  }

  private PublicKey generatePublicKey(String nStr, String eStr)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] nBytes = Base64.getUrlDecoder().decode(nStr);
    byte[] eBytes = Base64.getUrlDecoder().decode(eStr);

    BigInteger n = new BigInteger(1, nBytes);
    BigInteger e = new BigInteger(1, eBytes);

    RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(publicKeySpec);
  }

  /** 애플로 토큰 요청 시 사용할 Client Secret JWT 생성 */
  public String generateClientSecret() {
    try {
      long now = System.currentTimeMillis() / 1000;

      return Jwts.builder()
          .setHeaderParam("kid", keyId)
          .setHeaderParam("alg", "ES256")
          .setIssuer(teamId)
          .setIssuedAt(new Date(now * 1000))
          .setExpiration(new Date((now + 3600) * 1000)) // 1시간 후 만료
          .setAudience(audience)
          .setSubject(clientId)
          .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
          .compact();
    } catch (Exception e) {
      log.error("Apple Client Secret 생성 실패: {}", e.getMessage());
      throw new RuntimeException("Apple Client Secret 생성에 실패했습니다.", e);
    }
  }

  private PrivateKey getPrivateKey() throws Exception {
    String privateKeyContent =
        privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

    byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("EC");
    return keyFactory.generatePrivate(keySpec);
  }
}
