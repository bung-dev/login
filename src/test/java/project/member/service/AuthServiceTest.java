package project.member.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import project.member.domain.Refresh;
import project.member.domain.dto.TokenResponse;
import project.member.repository.RefreshRepository;
import project.member.security.jwt.JWTUtil;


import static org.assertj.core.api.Assertions.*;
import static project.member.CommonToken.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    RefreshRepository refreshRepository;
    @Autowired
    JWTUtil jwtUtil;

    @Test
    void reissue_success() {
        //given
        String loginId = "user1";
        String role = "ROLE_MEMBER";
        String oldRefresh = jwtUtil.createToken(loginId, role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        long future = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        Refresh refresh = refreshRepository.save(Refresh.create(loginId, oldRefresh, future));

        //when
        TokenResponse reissue = authService.reissue(refresh.getRefresh());

        //then
        assertThat(refreshRepository.findByRefresh(oldRefresh)).isEmpty();
        assertThat(reissue.refreshToken()).isNotEqualTo(oldRefresh);

        Refresh updated = refreshRepository.findByRefresh(reissue.refreshToken()).orElseThrow();

        assertThat(updated.getLoginId()).isEqualTo(loginId);
        assertThat(updated.getExpiration()).isGreaterThan(System.currentTimeMillis());
    }
}
