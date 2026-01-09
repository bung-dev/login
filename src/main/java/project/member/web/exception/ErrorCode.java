package project.member.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_404","존재하지 않는 회원입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER_401", "인증되지 않은 요청입니다");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
