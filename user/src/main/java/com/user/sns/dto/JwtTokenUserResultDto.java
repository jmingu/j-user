package com.user.sns.dto;

import com.user.sns.dto.response.LoginTokenDto;
import lombok.Getter;

@Getter
public class JwtTokenUserResultDto {

    private int resultCode;
    private String resultMessage;
    private LoginTokenDto result;
}
