package com.user.common.feign;

import com.user.sns.dto.JwtTokenUserDto;
import com.user.sns.dto.JwtTokenUserResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "jwtToken", url = "${service.j-authentication-service}")
public interface JwtTokenFeignClient {
    @PostMapping("/api/jwt")
    ResponseEntity<JwtTokenUserResultDto> getJwtToken(@RequestBody JwtTokenUserDto jwtTokenUserDto);
}


