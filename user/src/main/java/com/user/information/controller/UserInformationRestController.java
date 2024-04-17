package com.user.information.controller;

import com.common.dto.CommonResponseDto;
import com.common.entity.UserEntity;
import com.common.property.JwtProperty;
import com.user.common.configuration.util.CryptoUtil;
import com.user.common.configuration.util.JwtUtil;
import com.user.information.dto.UserDto;
import com.user.information.dto.UserJoinDto;
import com.user.information.dto.request.UserCreateNickNameRequestDto;
import com.user.information.dto.request.UserCreateRequestDto;
import com.user.information.dto.request.UserLoginRequestDto;
import com.user.information.dto.request.UserRefreshTokenDto;
import com.user.information.dto.response.UserListResponseDto;
import com.user.information.dto.response.UserResponseDto;
import com.user.information.service.UserInformationService;
import com.user.sns.dto.response.LoginTokenDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserInformationRestController {
    private final UserInformationService userInformationService;

    private final JwtUtil jwtUtil;
    private final JwtProperty jwtProperty;

    /**
     * 회원정보 조회(로그인정보)
     */
    @GetMapping("/user/token")
    public ResponseEntity<CommonResponseDto> findHeaderLoginId(HttpServletRequest request) throws Exception {
        // 헤더 정보
        final String header = request.getHeader("X-Auth-Status");

        // 헤더에 user_id 존재
        long userId = Long.parseLong(CryptoUtil.decrypt(header));

        UserDto userDto = userInformationService.findUserId(userId);

        UserResponseDto responseDto = UserResponseDto.builder()
                .userName(userDto.getUserName())
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .gender(userDto.getGender())
                .build();

        return CommonResponseDto.success(responseDto);
    }

    /**
     * 회원정보 조회(login_id)
     */
    @GetMapping("/user/login")
    public ResponseEntity<CommonResponseDto> findLoginId(@RequestParam String loginId) {

        UserDto userDto = userInformationService.findLoginId(loginId);

        UserResponseDto responseDto = UserResponseDto.builder()
                .userId(userDto.getUserId())
                .loginId(userDto.getLoginId())
                .userName(userDto.getUserName())
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .gender(userDto.getGender())
                .loginTypeId(userDto.getLoginTypeId())
                .build();

        return CommonResponseDto.success(responseDto);
    }

    /**
     * 회원정보 조회(user_id)
     */
    @GetMapping("/user")
    public ResponseEntity<CommonResponseDto> findUserId(@RequestParam Long userId) {

        UserDto userDto = userInformationService.findUserId(userId);

        UserResponseDto responseDto = UserResponseDto.builder()
                .userId(userDto.getUserId())
                .loginId(userDto.getLoginId())
                .userName(userDto.getUserName())
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .gender(userDto.getGender())
                .loginTypeId(userDto.getLoginTypeId())
                .build();

        return CommonResponseDto.success(responseDto);
    }

    /**
     * 회원정보 조회(user_id List)
     */
    @GetMapping("/user/list")
    public ResponseEntity<CommonResponseDto> findUserIdList(@RequestParam List<Long> userIdList) {

        List<UserDto> userDtoList = userInformationService.findUserIdList(userIdList);

        List<UserResponseDto> userResponseDto = new ArrayList<>();
        for (UserDto userDto : userDtoList) {
            UserResponseDto responseDto = UserResponseDto.builder()
                    .userId(userDto.getUserId())
                    .loginId(userDto.getLoginId())
                    .userName(userDto.getUserName())
                    .nickname(userDto.getNickname())
                    .email(userDto.getEmail())
                    .gender(userDto.getGender())
                    .loginTypeId(userDto.getLoginTypeId())
                    .build();

            userResponseDto.add(responseDto);
        }

        UserListResponseDto userList = UserListResponseDto.builder()
                .userList(userResponseDto)
                .build();


        return CommonResponseDto.success(userList);
    }

    /**
     * 닉네임 등록
     */
    @PostMapping("/user/nickname")
    public ResponseEntity<CommonResponseDto> createNickname(@RequestBody UserCreateNickNameRequestDto userCreateNickNameRequestDto, HttpServletRequest request) throws Exception {

        // 헤더 정보
        final String header = request.getHeader("X-Auth-Status");
        long userId = Long.parseLong(CryptoUtil.decrypt(header));

        userInformationService.createNickname(userCreateNickNameRequestDto.getNickname(), header);

        UserDto userDto = userInformationService.findUserId(userId);
        log.debug("userDto ====> {}", userDto);
        UserResponseDto responseDto = UserResponseDto.builder()
                .userName(userDto.getUserName())
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .gender(userDto.getGender())
                .build();

        return CommonResponseDto.success(responseDto);
    }

    /**
     * 로그인 아이디 중복검사
     */
    @GetMapping("/user/join/login-check")
    public ResponseEntity<CommonResponseDto> findLoginIdCheck(@RequestParam String loginId) {

        userInformationService.findLoginIdCheck(loginId);

        Map result = new HashMap<>();

        result.put("loginId", loginId);

        return CommonResponseDto.success(result);
    }

    /**
     * 회원가입
     */
    @PostMapping("/user/join")
    public ResponseEntity<CommonResponseDto> saveUser(@RequestBody UserCreateRequestDto userCreateRequestDto){

        UserJoinDto userJoinDto = UserJoinDto.builder()
                .loginId(userCreateRequestDto.getLoginId())
                .userName(userCreateRequestDto.getUserName())
                .gender(userCreateRequestDto.getGender())
                .email(userCreateRequestDto.getEmail())
                .password(userCreateRequestDto.getPassword())
                .build();

        userInformationService.saveUser(userJoinDto);

        return CommonResponseDto.success(userJoinDto);
    }

    /**
     * 일반 로그인
     */
    @PostMapping("/user/login")
    public ResponseEntity<CommonResponseDto> userLogin(@RequestBody UserLoginRequestDto userLoginRequestDto) throws Exception {

        LoginTokenDto loginTokenDto = userInformationService.userLogin(userLoginRequestDto.getLoginId(), userLoginRequestDto.getPassword());

        return CommonResponseDto.success(loginTokenDto);
    }

    /**
     * 리프레스 토큰
     */
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponseDto> refreshToken(@RequestBody UserRefreshTokenDto userRefreshTokenDto) throws Exception {


        log.debug("userRefreshTokenDto ==> {}", userRefreshTokenDto);
//        LoginTokenDto loginTokenDto = userInformationService.userLogin(userLoginRequestDto.getLoginId(), userLoginRequestDto.getPassword());


        // 토큰 가져오기 (Bearer를 뺸다)
        String getToken = userRefreshTokenDto.getRefreshToken();

        // 복호화 해제
        final String decryptToken = decrypt(getToken, jwtProperty.getTokenDecryptKey());

        // 토큰유효 확인
        if (jwtUtil.isExpired(decryptToken, jwtProperty.getSecretKey())) {
            return CommonResponseDto.error(HttpStatus.UNAUTHORIZED.value(), "Token is invalid");
        }

        // 토큰의 사용자 아이디 가져오기
        String userId = jwtUtil.getUserName(decryptToken, jwtProperty.getSecretKey());

        UserEntity userEntity = new UserEntity(Long.parseLong(userId));

        // 엑세스토큰발행
        String accessToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userEntity, jwtProperty.getExpiredTime(), "accessToken"), jwtProperty.getTokenDecryptKey());

        Map result = new HashMap<>();
        result.put("accessToken", accessToken);
        return CommonResponseDto.success(result);
    }

    // 복호화
    private static String decrypt(String decryptedData, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(decryptedData));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
