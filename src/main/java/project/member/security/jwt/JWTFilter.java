package project.member.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.member.security.CustomMemberDetailsService;
import project.member.web.exception.ErrorCode;
import project.member.web.exception.ErrorResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static project.member.CommonToken.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final CustomMemberDetailsService customMemberDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT Filter started uri={}", request.getRequestURI());

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.info("JWT skip (no auth header) uri={}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
        String category = jwtUtil.getCategory(token);
        if (!category.equals(JWT_ACCESS_TOKEN_NAME)){
            writeError(response,ErrorCode.INVALID_TOKEN_CATEGORY);
            return;
        }


            String loginId = jwtUtil.getLoginId(token);

            UserDetails userDetails = customMemberDetailsService.loadUserByUsername(loginId);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            writeError(response, ErrorCode.JWT_EXPIRED);
            return;
        } catch (UsernameNotFoundException e) {
            writeError(response, ErrorCode.MEMBER_NOT_FOUND);
            return;
        } catch (JwtException | IllegalArgumentException e) {
            writeError(response, ErrorCode.INVALID_TOKEN);
            return;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.equals("/") || path.equals("/login")
                || path.equals("/error")
                || path.equals("/favicon.ico")
                || path.equals("/index")
                || path.startsWith("/css/")
                || path.startsWith("/js/");
    }


    private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse body = ErrorResponse.of(
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );

        objectMapper.writeValue(response.getWriter(), body);
    }
}


