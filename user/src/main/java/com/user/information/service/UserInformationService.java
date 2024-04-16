package com.user.information.service;

import com.common.entity.*;
import com.common.exception.JApplicationException;
import com.common.property.JwtProperty;
import com.user.common.configuration.util.CryptoUtil;
import com.user.common.configuration.util.JwtUtil;
import com.user.information.dto.UserDto;
import com.user.information.dto.UserJoinDto;
import com.user.information.repository.UserInfomationRepository;
import com.user.sns.dto.response.LoginTokenDto;
import com.user.sns.repository.LoginHistoryDetailEntityRepository;
import com.user.sns.repository.LoginHistoryEntityRepository;
import com.user.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserInformationService {

    private final UserInfomationRepository userInfomationRepository;
    private final UserEntityRepository userEntityRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final LoginHistoryDetailEntityRepository loginHistoryDetailEntityRepository;
    private final LoginHistoryEntityRepository loginHistoryEntityRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperty jwtProperty;


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
                .loginTypeId(userEntity.getLoginTypeEntity().getLoginTypeId())
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
                .loginTypeId(userEntity.getLoginTypeEntity().getLoginTypeId())
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
                    .loginTypeId(userEntity.getLoginTypeEntity().getLoginTypeId())
                    .build();
            userDtos.add(userDto);
        }

        return userDtos;
    }

    /**
     * 닉네임 등록
     */
    @Transactional
    public void createNickname(String userNickname, String header) throws Exception {

        if (userNickname == null) {
            throw new JApplicationException("닉네임이 없습니다.");
        }

        // 헤더에 user_id 존재
        long userId = Long.parseLong(CryptoUtil.decrypt(header));
        if (userId < 0) {
            throw new JApplicationException("로그인이 필요합니다.");
        }

        // 중복 닉네임 검사
        UserEntity userEntity = userInfomationRepository.findByNickname(userNickname);
        log.debug("닉네임 조회 ==> {}" , userEntity);
        if (userEntity != null) {
            throw new JApplicationException("이미 등록된 닉네임이 있습니다.");
        }

        userInfomationRepository.editNickname(userNickname, LocalDateTime.now(), "userId : " + userNickname, userId);
    }

    /**
     * 로그인 아이디 중복검사
     */
    public void findLoginIdCheck(String loginId) {

        UserEntity userEntity = userInfomationRepository.findByLoginId(loginId);
        if (userEntity != null) {
            throw new JApplicationException("이미 등록된 아이디가 있습니다.");
        }
    }

    /**
     * 회원가입
     */
    @Transactional
    public void saveUser(UserJoinDto userJoinDto) {

        if (userJoinDto.getLoginId() == null) {
            throw new JApplicationException("아이디를 확인해 주세요.");
        }
        else if (userJoinDto.getLoginId().length() == 0) {
            throw new JApplicationException("아이디를 확인해 주세요.");
        }
        else if (userJoinDto.getLoginId().length() < 5 || userJoinDto.getLoginId().length() > 15) {
            throw new JApplicationException("아이디는 5~15자로 입력해 주세요.");
        }
        else if (userJoinDto.getPassword() == null) {
            throw new JApplicationException("비밀번호를 확인해 주세요.");
        }
        else if (userJoinDto.getPassword().length() == 0) {
            throw new JApplicationException("비밀번호를 확인해 주세요.");
        }
        else if (userJoinDto.getPassword().length() < 5 || userJoinDto.getPassword().length() > 20) {
            throw new JApplicationException("비밀번호는 5~20자로 입력해 주세요.");
        }
        else if (userJoinDto.getUserName() == null) {
            throw new JApplicationException("이름을 확인해 주세요.");
        }
        else if (userJoinDto.getUserName().length() == 0) {
            throw new JApplicationException("이름을 확인해 주세요.");
        }
        else if (userJoinDto.getGender() == null) {
            throw new JApplicationException("성별을 확인해 주세요.");
        }
        else if (userJoinDto.getEmail() == null) {
            throw new JApplicationException("이메일을 확인해 주세요.");
        }

        if (!(userJoinDto.getGender().equals("M") || userJoinDto.getGender().equals("F"))) {
            throw new JApplicationException("성별을 확인해 주세요.");
        }

        UserEntity idCheck = userInfomationRepository.findByLoginId(userJoinDto.getLoginId());
        if (idCheck != null) {
            throw new JApplicationException("이미 등록된 아이디가 있습니다.");
        }

        LoginTypeEntity normal = LoginTypeEntity.builder()
                .loginTypeId(2) // normal
                .build();
        OrganizationEntity organizationEntity = OrganizationEntity.builder()
                .organizationId(1) // 기본
                .build();

        UserEntity userEntity = new UserEntity(
                userJoinDto.getLoginId(),
                userJoinDto.getUserName(),
                bCryptPasswordEncoder.encode(userJoinDto.getPassword()),
                userJoinDto.getEmail(),
                userJoinDto.getGender(),
                organizationEntity,
                normal
        );

        // 회원가입
        userEntityRepository.save(userEntity);
    }

    /**
     * 일반 로그인
     */
    @Transactional
    public LoginTokenDto userLogin(String loginId, String password) throws Exception {

        if (loginId == null) {
            throw new JApplicationException("아이디를 확인해 주세요.");
        }
        else if (loginId.length() == 0) {
            throw new JApplicationException("아이디를 확인해 주세요.");
        }
        else if (password == null) {
            throw new JApplicationException("비밀번호를 확인해 주세요.");
        }
        else if (password.length() == 0) {
            throw new JApplicationException("비밀번호를 확인해 주세요.");
        }

        UserEntity userEntity = userInfomationRepository.findByLoginId(loginId);
        if (userEntity == null) {
            log.error("아이디 없음");
            throw new JApplicationException("아이디 또는 비밀번호를 확인해 주세요.");
        }

        // 비밀번호 일치여부
        if (bCryptPasswordEncoder.matches(password, userEntity.getPassword()) == false) {
            log.error("비밀번호 불일치");
            throw new JApplicationException("아이디 또는 비밀번호를 확인해 주세요.");
        }

        // 로그인 이력 등록
        LoginHistoryEntity history = loginHistoryEntityRepository.findLoginHistory(userEntity.getUserId(), LocalDate.now());

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
        String accessToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userEntity, jwtProperty.getExpiredTime(), "accessToken"), jwtProperty.getTokenDecryptKey());
        // 리프레시 토큰발행
        String refreshToken = CryptoUtil.encrypt(jwtUtil.makeAuthToken(userEntity, jwtProperty.getExpiredRefreshTime(), "refreshToken"), jwtProperty.getTokenDecryptKey());

        return new LoginTokenDto(accessToken, refreshToken);
    }



}
