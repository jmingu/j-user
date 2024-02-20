package com.common.dto;

import lombok.Getter;

@Getter
public class UserAuthenticationResultDto {
    private int resultCode;
    private String resultMessage;
    private LoginIdDto result;
}
