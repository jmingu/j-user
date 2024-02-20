package com.user.information.service;

import com.common.entity.UserEntity;
import com.user.information.dto.UserDto;
import com.user.information.repository.UserInfomationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInformationService {

    private final UserInfomationRepository userInfomationRepository;


    public UserDto findUserId(String loginId) {

        UserEntity userEntity = userInfomationRepository.findByLoginId(loginId).get();

        UserDto userDto = UserDto.builder()
                .userId(userEntity.getUserId())
                .loginId(userEntity.getLoginId())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .gender(userEntity.getGender())
                .loginType(userEntity.getLoginType())
                .build();

        return userDto;
    }
}
