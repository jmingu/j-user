package com.authentication.access.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAccessResponseDto {
    private Long userId;

    private String loginId;

    private String userName;

    private String email;

    private String gender;

    private String loginType;
}
