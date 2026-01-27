package project.member.web.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import project.member.web.exception.ErrorCode;

import java.util.Arrays;

import static project.member.CommonToken.JWT_COOKIE_REFRESH_TOKEN_EXPIRED_TIME;
import static project.member.CommonToken.JWT_REFRESH_TOKEN_NAME;

@Component
public class CookieUtil {
    public String createCookie(String value) {
        return ResponseCookie.from(JWT_REFRESH_TOKEN_NAME, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(JWT_COOKIE_REFRESH_TOKEN_EXPIRED_TIME)
                .build()
                .toString();
    }

    public String readCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> JWT_REFRESH_TOKEN_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public String removeCookie(){
        return ResponseCookie.from(JWT_REFRESH_TOKEN_NAME,"")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build()
                .toString();
    }
}
