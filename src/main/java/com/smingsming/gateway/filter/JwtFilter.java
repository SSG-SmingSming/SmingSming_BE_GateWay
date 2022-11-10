package com.smingsming.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smingsming.gateway.exception.ErrorCode;
import com.smingsming.gateway.filter.AuthorizationHeaderFilter;
import com.smingsming.gateway.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
@Slf4j
public class JwtFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;


    public JwtFilter() {
        super(AuthorizationHeaderFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(AuthorizationHeaderFilter.Config config) {
        return  ((exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().get("Authorization").get(0);
            String userPk = jwtTokenProvider.getUserPk(token);

            addAuthorizationHeaders(exchange.getRequest(), userPk);

            return chain.filter(exchange);
        });
//        return null;
    }


    // 성공적으로 검증이 되었기 때문에 인증된 헤더로 요청을 변경해준다. 서비스는 해당 헤더에서 아이디를 가져와 사용한다.
    private void addAuthorizationHeaders(ServerHttpRequest request, String userInfo) {
        request.mutate()
                .header("X-Authorization-Id", userInfo)
                .build();
    }

    @Bean
    public ErrorWebExceptionHandler tokenValidation() {
        return new JwtTokenExceptionHandler(objectMapper);
    }

    // 실제 토큰이 null, 만료 등 예외 상황에 따른 예외처리
    @RequiredArgsConstructor
    public class JwtTokenExceptionHandler implements ErrorWebExceptionHandler {

        private final ObjectMapper objectMapper;
        private String getErrorCode(int errorCode) {
            return "{ errorCode : " + errorCode + " }";
        }

        @Override
        public Mono<Void> handle(
                ServerWebExchange exchange, Throwable ex) {

            HashMap<String, String> errorMap = new HashMap<>();
            int errorCode = 401;

            if (ex.getClass() == NullPointerException.class) {
                errorMap.put("ErrorCode", ErrorCode.TOKEN_NULL.getHttpStatus().toString());
                errorMap.put("ErrorMsg", ErrorCode.TOKEN_NULL.getDetail());
            } else if (ex.getClass() == ExpiredJwtException.class) {
                errorMap.put("ErrorCode", ErrorCode.TOKEN_EXPIRED.getHttpStatus().toString());
                errorMap.put("ErrorMsg", ErrorCode.TOKEN_EXPIRED.getDetail());
            } else if (ex.getClass() == MalformedJwtException.class) {
                errorMap.put("ErrorCode", ErrorCode.TOKEN_WRONG_TYPE.getHttpStatus().toString());
                errorMap.put("ErrorMsg", ErrorCode.TOKEN_WRONG_TYPE.getDetail());
            } else if (ex.getClass() == SignatureException.class) {
                errorMap.put("ErrorCode",ErrorCode.TOKEN_SIGNATURE_ERROR.getHttpStatus().toString());
                errorMap.put("ErrorMsg", ErrorCode.TOKEN_SIGNATURE_ERROR.getDetail());
            } else if (ex.getClass() == UnsupportedJwtException.class) {
                errorMap.put("ErrorCode",ErrorCode.TOKEN_UNSUPPORTED.getHttpStatus().toString());
                errorMap.put("ErrorMsg", ErrorCode.TOKEN_UNSUPPORTED.getDetail());
            }
//            else if (ex.getClass() == HttpClientErrorException.Unauthorized) {
//                errorMap.put("ErrorCode",ErrorCode.INVALID_AUTH_TOKEN.getHttpStatus().toString());
//                errorMap.put("ErrorMsg", ErrorCode.INVALID_AUTH_TOKEN.getDetail());
//            }
            else {
                errorMap.put("ErrorCode",ErrorCode.UNAUTHORIZED_MEMBER.getHttpStatus().toString());
                errorMap.put("ErrorMsg", ErrorCode.UNAUTHORIZED_MEMBER.getDetail());
            }


            String error = "Gateway Error";
            try {
                error = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorMap);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException : " + e.getMessage());
            }


            byte[] bytes = error.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }
}


