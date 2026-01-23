package project.member.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.member.domain.dto.LoginRequest;
import project.member.domain.dto.TokenResponse;
import project.member.service.AuthService;

import static project.member.CommonToken.JWT_COOKIE_REFRESH_TOKEN_EXPIRED_TIME;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse token = authService.login(request);
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie(token.refreshToken()));
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .body(token);
    }

    protected String createCookie(String value) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(JWT_COOKIE_REFRESH_TOKEN_EXPIRED_TIME)
                .build()
                .toString();
    }
}
