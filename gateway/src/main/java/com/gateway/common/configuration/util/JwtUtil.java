package com.gateway.common.configuration.util;

import com.common.property.JwtProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperty jwtProperty;

    /**
     * 토큰생성
     */
    public String makeAuthToken(Map<String, Object> userClaims, int tokeTime) {
        Claims claims = Jwts.claims();

        for (Map.Entry<String, Object> entrySet : userClaims.entrySet()) {
            claims.put(entrySet.getKey(), entrySet.getValue());
        }

        // 현재 날짜와 시간 가져오기
        Date currentDate = new Date();

        // Calendar 객체 생성 및 현재 날짜 및 시간으로 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        // 분 추가
        calendar.add(Calendar.MINUTE, tokeTime);
        // 변경된 날짜 및 시간 가져오기
        Date newDate = calendar.getTime();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate) // 발생시간
                .setExpiration(newDate) // 만료시간
                .signWith(getKey(jwtProperty.getSecretKey()), SignatureAlgorithm.HS256)
                .compact(); // String으로 만듬 base64로 인코딩;
    }


    // Key값 만들기
    private Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes); // 시크릿키로 변환
    }

    /**
     * 토큰 검증
     */
    public boolean isExpired(String token, String key) {
        try {
            log.debug("token ==> {}", token);
            // 토큰 시간확인
            Date expiredDate = Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody().getExpiration(); //body에서 유효시간;
            log.debug("expiredDate ==> {}", expiredDate);
            return expiredDate.before(new Date()); // 현재시간보다 더 전인가
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 경우 true 반환
            return true;
        }
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