package com.user.information.controller;

import com.common.dto.CommonResponseDto;
import com.user.information.dto.UserDto;
import com.user.information.dto.response.UserResponseDto;
import com.user.information.service.UserInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserInformationRestController {
    private final UserInformationService userInformationService;

    @GetMapping("/user/{loginId}")
    public ResponseEntity<CommonResponseDto> findUserId(@PathVariable String loginId) {

        UserDto userDto = userInformationService.findUserId(loginId);

        UserResponseDto responseDto = UserResponseDto.builder()
                .userId(userDto.getUserId())
                .loginId(userDto.getLoginId())
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .gender(userDto.getGender())
                .loginType(userDto.getLoginType())
                .build();

        return CommonResponseDto.success(responseDto);
    }
}
