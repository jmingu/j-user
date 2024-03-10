package com.user.sns.service;

import com.common.entity.*;
import com.common.property.JwtProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.common.configuration.util.CryptoUtil;
import com.user.common.configuration.util.JwtUtil;
import com.user.common.feign.NidNaverFeignClient;
import com.user.common.feign.OpenApiNaverFeignClient;
import com.user.common.properties.Oauth2NaverRegistrationProperties;
import com.user.sns.dto.OAuth2NaverLoginResultDto;
import com.user.sns.dto.OAuth2NaverLoginTokenDto;
import com.user.sns.dto.response.LoginTokenDto;
import com.user.sns.repository.LoginHistoryDetailEntityRepository;
import com.user.sns.repository.LoginHistoryEntityRepository;
import com.user.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuth2LoginService {
    private final JwtUtil jwtUtil;

    private final JwtProperty jwtProperty;

    private final Oauth2NaverRegistrationProperties oauth2NaverRegistrationProperties;
    private final UserEntityRepository userEntityRepository;
    private final LoginHistoryEntityRepository loginHistoryEntityRepository;

    private final LoginHistoryDetailEntityRepository loginHistoryDetailEntityRepository;

    private final NidNaverFeignClient nidNaverFeignClient;
    private final OpenApiNaverFeignClient openApiNaverFeignClient;

    @Transactional
    public LoginTokenDto getToken(String code, String state) throws Exception {

        ResponseEntity<String> token = nidNaverFeignClient.getToken(oauth2NaverRegistrationProperties.getAuthorizationGrantType(),
                oauth2NaverRegistrationProperties.getClientId(),
                oauth2NaverRegistrationProperties.getClientSecret(),
                code,
                state);
        log.debug("token ==> {}", token.getBody());

        // 토큰값 Json 형식으로 가져오기위해 생성
        ObjectMapper objectMapper = new ObjectMapper();
        OAuth2NaverLoginTokenDto oAuth2NaverLoginTokenDto = objectMapper.readValue(token.getBody(), OAuth2NaverLoginTokenDto.class);
        log.debug("oAuth2LoginToken ==> {} " , oAuth2NaverLoginTokenDto);

        ResponseEntity<String> result = openApiNaverFeignClient.getResult("Bearer " + oAuth2NaverLoginTokenDto.getAccessToken());
        log.debug("result ==> {} " , result);

        // 토큰을 사용하여 사용자 정보 추출
        OAuth2NaverLoginResultDto oAuth2NaverLoginResultDto = objectMapper.readValue(result.getBody(), OAuth2NaverLoginResultDto.class);
        log.debug("naver login info ==> {}" , oAuth2NaverLoginResultDto);

        // 아이디 조회 후 없을 시 회원가입
        long loginIdCnt = userEntityRepository.countByLoginId(oAuth2NaverLoginResultDto.getResponse().getId());
        if (loginIdCnt == 0) {
            log.debug("loginId is null");
            LoginTypeEntity naver = userEntityRepository.findLoginType("NAVER");

            OrganizationEntity organizationEntity = OrganizationEntity.builder()
                    .organizationId(1)
                    .organizationName("기본")
                    .organizationLevel(0)
                    .build();

            UserEntity userEntity = new UserEntity(
                    oAuth2NaverLoginResultDto.getResponse().getId(),
                    oAuth2NaverLoginResultDto.getResponse().getName(),
                    oAuth2NaverLoginResultDto.getResponse().getEmail(),
                    oAuth2NaverLoginResultDto.getResponse().getEmail(),
                    oAuth2NaverLoginResultDto.getResponse().getGender(),
                    organizationEntity,
                    naver
            );

            // 회원가입
            userEntityRepository.save(userEntity);
        }

        // 사용자 정보 받아오기
        UserEntity userEntity = userEntityRepository.findByLoginId(oAuth2NaverLoginResultDto.getResponse().getId());

        // 로그인 이력 등록
        LoginHistoryEntity history = loginHistoryEntityRepository.findLoginHistory(userEntity.getUserId(),LocalDate.now());

        log.debug("history ==> {}", history);

        // 금일 로그인 기록이 없다면 로그인 등록, 로그인 상세 등록
        if (history == null) {
            LoginHistoryEntity loginHistory = new LoginHistoryEntity(userEntity);
            LoginHistoryEntity loginHistoryEntity = loginHistoryEntityRepository.save(loginHistory);

            LoginHistoryDetailEntity newLoginHistoryDetail = new LoginHistoryDetailEntity(loginHistoryEntity, userEntity.getUserId());
            // 로그인 상세 이력 등록
            loginHistoryDetailEntityRepository.save(newLoginHistoryDetail);
        }
        // 금일 로그인 기록이 있다면 로그인 상세만 등록
        else {
            LoginHistoryDetailEntity loginHistoryDetail = new LoginHistoryDetailEntity(history, userEntity.getUserId());
            // 로그인 상세 이력 등록
            loginHistoryDetailEntityRepository.save(loginHistoryDetail);
        }

        // 엑세스토큰발행
        String accessToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userEntity, jwtProperty.getExpiredTime()), jwtProperty.getTokenDecryptKey());
        // 리프레시 토큰발행
        String refreshToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userEntity, jwtProperty.getExpiredRefreshTime()), jwtProperty.getTokenDecryptKey());

        return new LoginTokenDto(accessToken, refreshToken);
    }
}
