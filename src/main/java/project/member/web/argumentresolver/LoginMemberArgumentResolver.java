package project.member.web.argumentresolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import project.member.domain.dto.MemberResponse;
import project.member.web.SessionConst;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = MemberResponse.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter,
                                            @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        HttpSession session = request.getSession(false);
        if (session == null){
            return null;
        }
        return session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
