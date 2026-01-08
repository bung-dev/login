package project.member.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import project.member.web.SessionConst;


@Log4j2
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        HttpSession session = request.getSession();

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            return false;
        }
        return true;
    }
}
