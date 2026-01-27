package project.member.service;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import project.member.domain.Member;
import project.member.domain.Refresh;
import project.member.domain.Role;
import project.member.domain.dto.LoginRequest;
import project.member.domain.dto.TokenResponse;
import project.member.repository.MemberRepository;
import project.member.repository.RefreshRepository;
import project.member.security.jwt.JWTUtil;
import project.member.web.exception.CustomException;
import project.member.web.exception.ErrorCode;


import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static project.member.CommonToken.*;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);
    @Autowired
    AuthService authService;
    @Autowired
    RefreshRepository refreshRepository;
    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;

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

    @Test
    void reissue_rotate() {
        //given
        String loginId = "user112345";
        String role = "ROLE_MEMBER";
        String oldRefresh = jwtUtil.createToken(loginId, role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        long future = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        Refresh refresh = refreshRepository.save(Refresh.create(loginId, oldRefresh, future));

        //when
        TokenResponse reissue = authService.reissue(refresh.getRefresh());
        TokenResponse newReissue = authService.reissue(refresh.getRefresh());

        List<Refresh> loginIdReissue = refreshRepository.findByLoginId(loginId);

        //then
        assertThat(loginIdReissue).hasSize(1);
        assertThat(loginIdReissue.get(0).getRefresh()).isEqualTo(newReissue.refreshToken());
        assertThat(loginIdReissue.get(0).getRefresh()).isNotEqualTo(reissue.refreshToken());
    }


    @Test
    void reissue_fail_REFRESH_TOKEN_INVALID() {
        //given
        String loginId = "user12";
        String role = "ROLE_MEMBER";
        String oldRefresh = jwtUtil.createToken(loginId, role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        long future = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;

        //when,then
        assertThatThrownBy(() -> {
            authService.reissue(oldRefresh);
        })
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_INVALID.exception().getMessage());
    }

    @Test
    void reissue_fail_REFRESH_TOKEN_EXPIRED() {
        //given
        String loginId = "user1234";
        String role = "ROLE_MEMBER";
        String oldRefresh = jwtUtil.createToken(loginId, role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        long future = System.currentTimeMillis() + -1;
        refreshRepository.save(Refresh.create(loginId, oldRefresh, future));
        //when,then
        assertThatThrownBy(() -> {
            authService.reissue(oldRefresh);
        })
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_EXPIRED.exception().getMessage());
        assertThat(refreshRepository.findByRefresh(oldRefresh)).isEmpty();
    }

    @Test
    void reissue_fail_REFRESH_TOKEN_MISMATCH() {
        //given
        String loginId = "user12345";
        String role = "ROLE_MEMBER";
        String oldRefresh = jwtUtil.createToken("use32231", role, JWT_REFRESH_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        long future = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        refreshRepository.save(Refresh.create(loginId, oldRefresh, future));
        //when,then
        assertThatThrownBy(() -> {
            authService.reissue(oldRefresh);
        })
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_MISMATCH.exception().getMessage());
    }

    @Test
    void reissue_fail_INVALID_TOKEN_CATEGORY() {
        //given
        String loginId = "user123456";
        String role = "ROLE_MEMBER";
        String oldRefresh = jwtUtil.createToken(loginId, role, JWT_ACCESS_TOKEN_NAME, JWT_REFRESH_TOKEN_EXPIRED_TIME);

        long future = System.currentTimeMillis() + JWT_REFRESH_TOKEN_EXPIRED_TIME;
        refreshRepository.save(Refresh.create(loginId, oldRefresh, future));
        //when,then
        assertThatThrownBy(() -> {
            authService.reissue(oldRefresh);
        })
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN_CATEGORY.exception().getMessage());
    }

    @Test
    void login_success(){
        //given
        String loginId = "user123456";
        String password = "password1234";
        String name = "user";

        String encode = passwordEncoder.encode(password);

        Member member = Member.builder()
                .loginId(loginId)
                .password(encode)
                .name(name)
                .build();

        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest(loginId, password);

        //when
        TokenResponse login = authService.login(loginRequest);

        //then
        assertThat(login.accessToken()).isNotBlank();
        assertThat(login.refreshToken()).isNotBlank();

        //DB saved
        List<Refresh> saved = refreshRepository.findByLoginId(loginId);
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getRefresh()).isEqualTo(login.refreshToken());

        //RTR
        TokenResponse newLogin = authService.reissue(saved.get(0).getRefresh());
        refreshRepository.findByLoginId(loginId);
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getRefresh()).isNotEqualTo(login.refreshToken());
        assertThat(saved.get(0).getRefresh()).isEqualTo(newLogin.refreshToken());

        //refresh token claim
        assertThat(jwtUtil.getLoginId(login.refreshToken())).isEqualTo(loginId);
        assertThat(jwtUtil.getCategory(login.refreshToken())).isEqualTo(JWT_REFRESH_TOKEN_NAME);
        assertThat(jwtUtil.getRole(login.refreshToken())).isEqualTo(Role.ROLE_MEMBER.name());

        //access token claim
        assertThat(jwtUtil.getLoginId(login.accessToken())).isEqualTo(loginId);
        assertThat(jwtUtil.getCategory(login.accessToken())).isEqualTo(JWT_ACCESS_TOKEN_NAME);
        assertThat(jwtUtil.getRole(login.accessToken())).isEqualTo(Role.ROLE_MEMBER.name());
    }

    @Test
    void login_fail(){
        //given
        String loginId = "user123456";
        String password = "password1234";
        String name = "user";

        String encode = passwordEncoder.encode(password);

        Member member = Member.builder()
                .loginId(loginId)
                .password(encode)
                .name(name)
                .build();

        memberRepository.save(member);

        //when, then
        assertThatThrownBy(() -> {
            authService.login(new LoginRequest(loginId, "pwjdhansq"));
        })
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_CREDENTIALS.exception().getMessage());
    }
}
