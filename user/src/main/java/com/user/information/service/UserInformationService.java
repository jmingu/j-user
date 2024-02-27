package com.user.information.service;

import com.common.entity.UserEntity;
import com.user.information.dto.UserDto;
import com.user.information.repository.UserInfomationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInformationService {

    private final UserInfomationRepository userInfomationRepository;


    /**
     * 회원정보 조회(login_id)
     */
    public UserDto findLoginId(String loginId) {

        UserEntity userEntity = userInfomationRepository.findByLoginId(loginId);

        UserDto userDto = UserDto.builder()
                .userId(userEntity.getUserId())
                .loginId(userEntity.getLoginId())
                .userName(userEntity.getUserName())
                .nickname(userEntity.getNickname())
                .email(userEntity.getEmail())
                .gender(userEntity.getGender())
                .loginType(userEntity.getLoginType())
                .build();

        return userDto;
    }

    /**
     * 회원정보 조회(user_id)
     */
    public UserDto findUserId(Long userId) {

        UserEntity userEntity = userInfomationRepository.findByUserId(userId);

        UserDto userDto = UserDto.builder()
                .userId(userEntity.getUserId())
                .loginId(userEntity.getLoginId())
                .userName(userEntity.getUserName())
                .nickname(userEntity.getNickname())
                .email(userEntity.getEmail())
                .gender(userEntity.getGender())
                .loginType(userEntity.getLoginType())
                .build();

        return userDto;
    }

    /**
     * 사용자 리스트 조회
     */
    public List<UserDto> findUserIdList(List<Long> userIdList) {

        List<UserEntity> userEntityList = userInfomationRepository.findByLoginList(userIdList);

        List<UserDto> userDtos = new ArrayList<>();

        for (UserEntity userEntity : userEntityList) {
            UserDto userDto = UserDto.builder()
                    .userId(userEntity.getUserId())
                    .loginId(userEntity.getLoginId())
                    .userName(userEntity.getUserName())
                    .nickname(userEntity.getNickname())
                    .email(userEntity.getEmail())
                    .gender(userEntity.getGender())
                    .loginType(userEntity.getLoginType())
                    .build();
            userDtos.add(userDto);
        }

        return userDtos;
    }
}
