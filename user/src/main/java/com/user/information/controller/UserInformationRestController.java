package com.user.information.controller;

import com.common.dto.CommonResponseDto;
import com.user.common.configuration.util.CryptoUtil;
import com.user.information.dto.UserDto;
import com.user.information.dto.response.UserResponseDto;
import com.user.information.service.UserInformationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        String loginId = CryptoUtil.decrypt(header);
        UserDto userDto = userInformationService.findLoginId(loginId);

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
}
