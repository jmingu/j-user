package com.user.information.dto.response;

import com.user.information.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponseDto {
    private List<UserDto> userList;
}
