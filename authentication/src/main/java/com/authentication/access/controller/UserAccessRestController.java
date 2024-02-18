package com.authentication.access.controller;

import com.authentication.access.dto.response.UserAccessResponseDto;
import com.authentication.access.service.UserAccessService;

import com.common.dto.CommonResponseDto;
import com.common.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserAccessRestController {
    private final UserAccessService userAccessService;

    @PostMapping("/user")
    public ResponseEntity<CommonResponseDto> userAccess(Principal principal) {

        UserEntity userEntity = (UserEntity) ((Authentication) principal).getPrincipal();
        log.debug("userEntity.getUserId() ==> {}", userEntity.getUserId());
        userAccessService.userAccess();

        UserAccessResponseDto userAccessResponseDto = UserAccessResponseDto.builder()
                .userId(userEntity.getUserId())
                .loginId(userEntity.getLoginId())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .gender(userEntity.getGender())
                .loginType(userEntity.getLoginType())
                .build();

        return CommonResponseDto.success(userAccessResponseDto);
    }


}
