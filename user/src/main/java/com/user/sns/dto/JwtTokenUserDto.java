package com.user.sns.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtTokenUserDto {
    private String loginId;
    private String userName;
}
