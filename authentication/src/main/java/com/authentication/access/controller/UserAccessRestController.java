package com.authentication.access.controller;

import com.authentication.access.dto.response.LoginIdResponsDto;
import com.authentication.access.dto.response.LoginTokenResponseDto;
import com.authentication.access.service.UserAccessService;
import com.authentication.common.configuration.util.JwtUtil;
import com.common.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
public class UserAccessRestController {
    private final UserAccessService userAccessService;

    private final JwtUtil jwtUtil;

    @Value("${jwt.token.expired-time}")
    private int expiredTime;
    @Value("${jwt.token.expired-refresh-time}")
    private int refreshTime;

    @PostMapping("/auth/token")
    public ResponseEntity<CommonResponseDto> userAccess(Principal principal) {

        LoginIdResponsDto loginIdResponsDto = LoginIdResponsDto.builder()
                .loginId(principal.getName())
                .build();
        return CommonResponseDto.success(loginIdResponsDto);
    }

    /**
     * 토큰발급
     */
    @PostMapping("/jwt")
    public ResponseEntity<CommonResponseDto> makeToken(@RequestBody Map<String, Object> userInfo) {


        // 엑세스토큰발행(1시간)
        String accessToken = jwtUtil.makeAuthToken(userInfo, expiredTime);
        // 리프레시 토큰발행(1달)
        String refreshToken = jwtUtil.makeAuthToken(userInfo, refreshTime);


        LoginTokenResponseDto responseDto = LoginTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return CommonResponseDto.success(responseDto);
    }


}
