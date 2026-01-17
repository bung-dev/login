package project.member.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorCode,
        String message,
        Map<String, String> errors) {
    public  static ErrorResponse of(String errorCode, String message, Map<String, String> errors) {
        return new ErrorResponse(errorCode, message, errors);
    }
}
