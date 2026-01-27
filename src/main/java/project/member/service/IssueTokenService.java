package project.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.domain.Refresh;
import project.member.domain.Role;
import project.member.domain.dto.TokenResponse;
import project.member.repository.RefreshRepository;
import project.member.security.jwt.JWTUtil;

import static project.member.CommonToken.*;
import static project.member.CommonToken.JWT_REFRESH_TOKEN_EXPIRED_TIME;

@Service
@RequiredArgsConstructor
public class IssueTokenService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Transactional
    public TokenResponse issueToken(String loginId){
        String role = Role.ROLE_MEMBER.name();

        String access = jwtUtil.createToken(loginId, role, JWT_ACCESS_TOKEN_NAME, JWT_ACCESS_TOKEN_EXPIRED_TIME);
        String refresh = jwtUtil.createToken(loginId, role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        refreshRepository.deleteAllByLoginId(loginId);
        addRefreshToken(loginId, refresh);

        return TokenResponse.from(access, refresh);
    }
    private void addRefreshToken(String loginId, String refreshToken) {
        long expiresAtMillis = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        Refresh refresh = Refresh.create(loginId, refreshToken, expiresAtMillis);
        refreshRepository.save(refresh);
    }
}
