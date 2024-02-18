package com.user.sns.service;

import com.common.entity.OrganizationEntity;
import com.common.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.common.configuration.util.JWTUtil;
import com.user.common.feign.NidNaverFeignClient;
import com.user.common.feign.OpenApiNaverFeignClient;
import com.user.common.properties.Oauth2NaverRegistrationProperties;
import com.user.sns.dto.OAuth2NaverLoginResultDto;
import com.user.sns.dto.OAuth2NaverLoginTokenDto;
import com.user.sns.dto.response.LoginTokenDto;
import com.user.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuth2LoginService {

    private final Oauth2NaverRegistrationProperties oauth2NaverRegistrationProperties;
    private final UserEntityRepository userEntityRepository;
    private final JWTUtil jwtUtil;
    @Value("${jwt.token.expired-time}")
    private int expiredTime;
    @Value("${jwt.token.expired-refresh-time}")
    private int refreshTime;
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

        log.debug("loginIdCnt ==> {}", loginIdCnt);
        if (loginIdCnt == 0) {
            log.debug("loginId is null");
            OrganizationEntity organizationEntity = OrganizationEntity.builder()
                    .organizationId(1)
                    .organizationName("기본")
                    .build();

            UserEntity userEntity = new UserEntity(
                    oAuth2NaverLoginResultDto.getResponse().getId(),
                    oAuth2NaverLoginResultDto.getResponse().getName(),
                    oAuth2NaverLoginResultDto.getResponse().getEmail(),
                    oAuth2NaverLoginResultDto.getResponse().getEmail(),
                    oAuth2NaverLoginResultDto.getResponse().getGender(),
                    "NAVER",
                    organizationEntity
            );

            // 회원가입
            userEntityRepository.save(userEntity);
        }

        UserEntity userEntity = userEntityRepository.findByLoginId(oAuth2NaverLoginResultDto.getResponse().getId()).get();

        // 엑세스토큰발행(1시간)
        String accessToken = jwtUtil.makeAuthToken(userEntity, expiredTime);
        // 리프레시 토큰발행(1달)
        String refreshToken = jwtUtil.makeAuthToken(userEntity, refreshTime);

        LoginTokenDto loginTokenDto = new LoginTokenDto(accessToken, refreshToken);

        return loginTokenDto;
    }
}
