package com.authentication.common.configuration.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
public class JWTUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;


    // Key값 만들기
    private Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes); // 시크릿키로 변환
    }

    /**
     * 토큰 검증
     */
    public boolean isExpired(String token, String key) {
        // 토큰 시간확인
        Date expiredDate = Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody().getExpiration(); //body에서 유효시간;
        log.debug("JWT ==> {}", Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody());
        log.debug("JWT ==> {}", Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody().get("loginId"));
        log.debug("JWT ==> {}", Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody().getExpiration());
        return expiredDate.before(new Date()); // 현재시간보다 더 전인가
    }

    // 토큰에서 claims 가져온다
    private Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody(); // body가 나온다


    }

    // 토큰에서 정보 가져오기
    public String getUserName(String token, String key) {
        return extractClaims(token, key).get("loginId", String.class); //body에서 loginId가져오기
    }



}