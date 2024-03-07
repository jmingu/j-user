package com.user.information.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long userId;

    private String loginId;

    private String userName;

    private String email;

    private String gender;

    private Long loginTypeId;

    private String nickname;
}
