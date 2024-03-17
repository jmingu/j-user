package com.user.information.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserJoinDto {

    private String loginId;

    private String userName;

    private String email;

    private String gender;

    private String password;

}
