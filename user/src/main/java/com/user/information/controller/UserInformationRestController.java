package com.user.information.controller;

import com.common.dto.CommonResponseDto;
import com.user.information.dto.UserDto;
import com.user.information.dto.response.UserResponseDto;
import com.user.information.service.UserInformationService;
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
                .loginType(userDto.getLoginType())
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
                .loginType(userDto.getLoginType())
                .build();

        return CommonResponseDto.success(responseDto);
    }
}
