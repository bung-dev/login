package project.member.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
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

    @Transactional
    public TokenResponse reissue(String refreshToken){
        Refresh saveRefresh = refreshRepository.findByRefresh(refreshToken)
                .orElseThrow(ErrorCode.REFRESH_TOKEN_INVALID::exception);

        long now = System.currentTimeMillis();
        if(saveRefresh.getExpiration() < now){
            refreshRepository.deleteByRefresh(refreshToken);
            throw ErrorCode.REFRESH_TOKEN_EXPIRED.exception();
        }
        String loginId;
        String role;

        try{
            if(jwtUtil.isExpired(refreshToken)){
                refreshRepository.deleteByRefresh(refreshToken);
                throw ErrorCode.REFRESH_TOKEN_EXPIRED.exception();
            }
            loginId = jwtUtil.getLoginId(refreshToken);
            role = jwtUtil.getRole(refreshToken);
        } catch (ExpiredJwtException e){
            throw ErrorCode.REFRESH_TOKEN_EXPIRED.exception();
        } catch (JwtException | IllegalArgumentException e){
            throw ErrorCode.REFRESH_TOKEN_INVALID.exception();
        }

        if (!saveRefresh.getLoginId().equals(loginId)) {
            throw ErrorCode.REFRESH_TOKEN_MISMATCH.exception();
        }

        String newAccessToken = jwtUtil.createToken(loginId, role, JWT_ACCESS_TOKEN_NAME, JWT_ACCESS_TOKEN_EXPIRED_TIME);
        String newRefreshToken = jwtUtil.createToken(loginId, role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        long newExpiresAtMillis = now + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        saveRefresh.changeExpiration(newExpiresAtMillis);
        saveRefresh.changeRefresh(newRefreshToken);

        return TokenResponse.from(newAccessToken,newRefreshToken);
    }

    @Transactional
    public void logout(String token){
        refreshRepository.deleteByRefresh(token);
    }

    private void addRefreshToken(String loginId, String refreshToken) {
        long expiresAtMillis = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        Refresh refresh = Refresh.create(loginId, refreshToken, expiresAtMillis);
        refreshRepository.save(refresh);
    }
}
