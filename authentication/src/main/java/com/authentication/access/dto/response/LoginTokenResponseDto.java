package com.authentication.access.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
