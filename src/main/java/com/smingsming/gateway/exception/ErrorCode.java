package com.smingsming.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    TOKEN_WRONG_TYPE(UNAUTHORIZED, "잘못된 형식의 토큰입니다."),
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_UNSUPPORTED(UNAUTHORIZED, "지원하지 않는 형식의 토큰입니다."),
    ACCESS_DENIED(UNAUTHORIZED, "접근할 수 없습니다."),
    TOKEN_SIGNATURE_ERROR(UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    TOKEN_NULL(UNAUTHORIZED, "토큰이 null이 아닙니다."),
//    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다."),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),


    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "이미 존재하는 데이터입니다."),


    /* 500 INTERNAL_SERVER_ERROR :  */
    USER_SERVER_ERROR(INTERNAL_SERVER_ERROR, "USER 서버에서 요청을 이행할 수 없습니다."),
    USERSERVICE_SERVER_ERROR(INTERNAL_SERVER_ERROR, "USER SERVICE 서버에서 요청을 이행할 수 없습니다."),
    CHAT_SERVER_ERROR(INTERNAL_SERVER_ERROR, "CHAT 서버에서 요청을 이행할 수 없습니다."),
    SONG_SERVER_ERROR(INTERNAL_SERVER_ERROR, "SONG 서버에서 요청을 이행할 수 없습니다."),


    /* 503 SERVICE_UNAVAILABLE :  */
    USER_SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "USER 서버가 지연되는 중입니다."),
    USERSERVICE_SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "USER SERVICE 서버가 지연되는 중입니다."),
    CHAT_SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "CHAT 서버가 지연되는 중입니다."),
    SONG_SERVICE_UNAVAILABLE(SERVICE_UNAVAILABLE, "SONG 서버가 지연되는 중입니다.");

   private final HttpStatus httpStatus;
    private final String detail;

}
