package project.member.service;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import project.member.domain.Refresh;
import project.member.domain.dto.LoginRequest;
import project.member.domain.dto.TokenResponse;
import project.member.repository.RefreshRepository;
import project.member.security.jwt.JWTUtil;
import project.member.web.exception.ErrorCode;

import static project.member.CommonToken.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public TokenResponse login(LoginRequest request) {
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.loginId(), request.password()));
            String loginId = auth.getName();

            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(ErrorCode.FORBIDDEN::exception)
                    .getAuthority();

            String accessToken = jwtUtil.createToken(loginId, role, JWT_ACCESS_TOKEN_NAME , JWT_ACCESS_TOKEN_EXPIRED_TIME);
            String refreshToken = jwtUtil.createToken(loginId, role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

            refreshRepository.deleteAllByLoginId(loginId);

            addRefreshToken(loginId,refreshToken);

            return TokenResponse.from(accessToken, refreshToken);

        } catch (BadCredentialsException e) {
            throw ErrorCode.INVALID_CREDENTIALS.exception();
        }
    }

    protected void addRefreshToken(String loginId, String refreshToken) {
        long expiresAtMillis = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        Refresh refresh = Refresh.create(loginId, refreshToken, expiresAtMillis);
        refreshRepository.save(refresh);
    }
}
