package com.user.information.controller;

import com.common.dto.CommonResponseDto;
import com.user.common.configuration.util.CryptoUtil;
import com.user.information.dto.UserDto;
import com.user.information.dto.UserJoinDto;
import com.user.information.dto.request.UserCreateNickNameRequestDto;
import com.user.information.dto.request.UserCreateRequestDto;
import com.user.information.dto.request.UserLoginRequestDto;
import com.user.information.dto.response.UserListResponseDto;
import com.user.information.dto.response.UserResponseDto;
import com.user.information.service.UserInformationService;
import com.user.sns.dto.response.LoginTokenDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserInformationRestController {
    private final UserInformationService userInformationService;

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

}
