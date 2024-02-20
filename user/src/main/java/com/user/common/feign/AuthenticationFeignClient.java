package com.user.common.feign;

import com.common.dto.UserAuthenticationResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "authentication", url = "${service.j-authentication-service}")
public interface AuthenticationFeignClient {
    @PostMapping("/api/auth/token")
    ResponseEntity<UserAuthenticationResultDto> getResult(@RequestHeader("Authorization") String token);

}
