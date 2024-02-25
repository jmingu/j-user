package com.user.sns.dto;

import com.user.sns.dto.response.LoginTokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenUserResultDto {

    private int resultCode;
    private String resultMessage;
    private LoginTokenDto result;
}
