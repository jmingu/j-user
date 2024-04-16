package com.user.common.configuration.util;

import com.common.entity.UserEntity;
import com.common.property.JwtProperty;
import io.jsonwebtoken.Claims;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperty jwtProperty;

    /**
     * 토큰생성
     */
    public String makeAuthToken(UserEntity userEntity, int tokeTime, String tokenClf) {
        Claims claims = Jwts.claims();
        claims.put("userId", userEntity.getUserId()+"");
        claims.put("tokenClf", tokenClf);

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
        log.debug("key ==> {}", key);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes); // 시크릿키로 변환
    }

}