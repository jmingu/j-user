package com.gateway.common.configuration.filter;


import com.gateway.common.configuration.util.CryptoUtil;
import com.gateway.common.configuration.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private JwtUtil jwtUtil;
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-decrypt-key}")
    private String tokenDecryptKey;

    public AuthorizationHeaderFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }
    public static class Config {
        // application.yml 파일에서 지정한 filer의 Argument값을 받는 부분
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            try{
                log.debug("request ==> {}", request.getPath());
                // 요청 경로가 /user/api/oauth/naver를 포함하는 경우(로그인) 통과
                if (request.getPath().toString().startsWith("/user/api/oauth/login")) {
                    return chain.filter(exchange);
                }

                // 헤더 정보
                log.debug("header ==> {}", request.getHeaders());
                // 토큰있는 헤더시작은 Bearer 로 시작한다.
                if(request.getHeaders() == null || !request.getHeaders().containsKey("authorization")){
                    log.error("Error header");
                    return errorResponse(exchange, HttpStatus.UNAUTHORIZED, null);
                }

                final String header = request.getHeaders().get("authorization").get(0);
                // 토큰 가져오기 (Bearer를 뺸다)
                String getToken = header.split(" ")[1].trim();

                // 복호화 헤저
                final String decryptToken = CryptoUtil.decrypt(getToken, tokenDecryptKey);

                // 토큰유효 확인
                if (jwtUtil.isExpired(decryptToken, secretKey)) {
                    return errorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token is invalid");
                }

                // 토큰의 사용자 아이디 가져오기
                String userName = jwtUtil.getUserName(decryptToken, secretKey);

                // 랜덤으로 16자리 암호화 키 만들기
                String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

                // 암호화
                String encrypt = CryptoUtil.encrypt(userName, randomString);

                // 검증 완료 헤더 추가
                request.mutate().header("X-Auth-Status", randomString + encrypt)
                        .build();

                return chain.filter(exchange); // 토큰이 일치할 때
            } catch (Exception e){
                return errorResponse(exchange, HttpStatus.UNAUTHORIZED, e.getMessage());
            }
        });
    }

    // 에러 반환
    private Mono<Void> errorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(status.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON); // JSON 응답을 설정

        if (message == null) {
            message = "token error";
        }

        // JSON 형식의 응답 본문을 생성
        String responseBody = String.format("{\"resultCode\": %d, \"resultMessage\": \"%s\"}", status.value(), message);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer)); // 응답 본문을 설정하고 Mono<Void> 반환
    }

}
