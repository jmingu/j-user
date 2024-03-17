package com.user.information.dto.request;

import lombok.Getter;

@Getter

public class UserCreateRequestDto {

    private String loginId;
    private String userName;
    private String gender;
    private String email;
    private String password;

}
