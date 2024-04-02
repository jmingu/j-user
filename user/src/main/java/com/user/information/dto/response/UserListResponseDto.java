package com.user.information.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponseDto {
    private List<UserResponseDto> userList;
}
