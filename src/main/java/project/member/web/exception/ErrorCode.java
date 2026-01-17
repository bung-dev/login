package project.member.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_404","존재하지 않는 회원입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER_401", "인증되지 않은 요청입니다"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "MEMBER_401_INVALID", "아이디 또는 비밀번호가 올바르지 않습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "MEMBER_409", "이미 사용중인 아이디입니다"),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    public CustomException exception() {
        return new CustomException(this);
    }
}
