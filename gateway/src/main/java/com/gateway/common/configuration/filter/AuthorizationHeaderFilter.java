package com.gateway.common.configuration.filter;


import com.common.property.JwtProperty;
import com.gateway.common.configuration.util.CryptoUtil;
import com.gateway.common.configuration.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    // 필터 없이 되야하는 추가 경로를 추가할 수 있습니다.
    private static final List<String> ALLOWED_PATHS = Arrays.asList(
            "/post/api/borads",
            "/post/api/comments",
            "/user/api/user/join",
            "/user/api/user/login"// 로그인 아이디 체크
    );

    private JwtUtil jwtUtil;
    private JwtProperty jwtProperty;

    public AuthorizationHeaderFilter(JwtUtil jwtUtil, JwtProperty jwtProperty) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.jwtProperty = jwtProperty;
    }

    public static class Config {
        // application.yml 파일에서 지정한 filer의 Argument값을 받는 부분
    }

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {

            try{
                ServerHttpRequest request = exchange.getRequest();

                // 로그인이면 그냥 통과
                if (request.getPath().toString().startsWith("/user/api/oauth/")) {
                    return chain.filter(exchange);
                }

                final String header = request.getHeaders().get("authorization").get(0);

                // 토큰 가져오기 (Bearer를 뺸다)
                String getToken = header.split(" ")[1].trim();

                // 필터링 로직(로그인 없이 봐도 되는 페이지) 
                if (isAllowedRequest(request)) {
                    AddHeader(request, null);
                    if (getToken == null || getToken.equals("undefined") || getToken.equals("null")) {
                        return chain.filter(exchange);
                    }

//                    // 로그인을 안했다면 로그인 아이디 임시로 음수로 주기위해 추가
//                    if (getToken == null || getToken.equals("undefined") || getToken.equals("null")) {
//                        // 검증헤더 추가
//                        AddHeader(request, null);
//                        return chain.filter(exchange);
//                    }
                }

                // 헤더 정보
                log.debug("header ==> {}", request.getHeaders());
                // 토큰있는 헤더시작은 Bearer 로 시작한다.

                if(request.getHeaders() == null || !request.getHeaders().containsKey("authorization")){
                    log.error("Error header");
                    return errorResponse(exchange, HttpStatus.UNAUTHORIZED, null);
                }

                // 복호화 해제
                final String decryptToken = CryptoUtil.decrypt(getToken, jwtProperty.getTokenDecryptKey());

                // 토큰유효 확인
                if (jwtUtil.isExpired(decryptToken, jwtProperty.getSecretKey())) {
                    return errorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token is invalid");
                }

                // 토큰의 사용자 아이디 가져오기
                String userId = jwtUtil.getUserName(decryptToken, jwtProperty.getSecretKey());

                // 검증헤더 추가
                AddHeader(request, userId);

                return chain.filter(exchange); // 토큰이 일치할 때
            } catch (Exception e){
                e.printStackTrace();
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

    // 통과로직
    private boolean isAllowedRequest(ServerHttpRequest request) {
        return ALLOWED_PATHS.stream()
                .anyMatch(path -> request.getPath().toString().startsWith(path));
    }

    // 헤더만들기
    private void AddHeader(ServerHttpRequest request, String userId) throws Exception {
        // 랜덤으로 16자리 암호화 키 만들기
        String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        if (userId == null) {
            userId = -1+"";
        }

        // 암호화
        String encrypt = CryptoUtil.encrypt(userId, randomString);

        // 검증 완료 헤더 추가
        request.mutate().header("X-Auth-Status", randomString + encrypt)
                .build();
    }

}
