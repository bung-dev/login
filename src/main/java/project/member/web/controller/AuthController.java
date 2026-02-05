package project.member.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.member.domain.dto.LoginRequest;
import project.member.domain.dto.TokenResponse;
import project.member.service.AuthService;
import project.member.web.exception.ErrorCode;
import project.member.web.util.CookieUtil;

@Tag(name = "Auth API", description = "로그인/토큰 재발급/로그아웃 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(
            summary = "로그인",
            description = """
                    아이디/비밀번호로 로그인하고 Access/Refresh 토큰을 발급한다.

                    - Access Token: 응답 헤더 Authorization: Bearer {accessToken}
                    - Refresh Token: HttpOnly 쿠키(Set-Cookie)
                    """,
            requestBody = @RequestBody(
                    required = true,
                    description = "로그인 요청 DTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공 (Access/Refresh 발급)",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "Bearer Access Token",
                                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
                                    ),
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "Refresh Token 쿠키",
                                            schema = @Schema(type = "string", example = "refresh=...; Path=/; HttpOnly;")
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
                    @ApiResponse(responseCode = "401", description = "로그인 실패(아이디/비밀번호 불일치)")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @org.springframework.web.bind.annotation.RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        TokenResponse token = authService.login(request);
        String cookie = cookieUtil.createCookie(token.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.accessToken())
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(token);
    }

    @Operation(
            summary = "토큰 재발급",
            description = """
                    Refresh Token 쿠키를 사용해 Access/Refresh 토큰을 재발급한다.

                    - 입력: 요청 쿠키의 refresh 값
                    - 출력:
                      - Authorization 헤더에 새 Access Token
                      - Set-Cookie 헤더로 새 Refresh Token(로테이션)
                    - 실패:
                      - refresh 쿠키가 없으면 401
                      - refresh가 만료/위조/불일치면 401
                    """,
            parameters = {
                    @Parameter(
                            name = "refresh",
                            in = ParameterIn.COOKIE,
                            required = true,
                            description = "Refresh Token 쿠키",
                            example = "eyJhbGciOiJIUzI1NiJ9..."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "재발급 성공 (Access/Refresh 갱신)",
                            headers = {
                                    @Header(
                                            name = "Authorization",
                                            description = "Bearer Access Token",
                                            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
                                    ),
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "새 Refresh Token 쿠키(로테이션)",
                                            schema = @Schema(type = "string", example = "refresh=...; Path=/; HttpOnly;")
                                    )
                            },
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "재발급 실패(쿠키 누락/만료/위조/불일치)"),
                    @ApiResponse(responseCode = "403", description = "권한 없음")
            }
    )
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = cookieUtil.readCookie(request);
        if (refresh == null) {
            throw ErrorCode.REFRESH_TOKEN_MISSING.exception();
        }
        TokenResponse newToken = authService.reissue(refresh);
        String cookie = cookieUtil.createCookie(newToken.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newToken.accessToken())
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(newToken);
    }

    @Operation(
            summary = "로그아웃",
            description = """
                    Refresh Token을 폐기하고(서버/DB) 클라이언트 쿠키도 만료 처리한다.

                    - 입력: 요청 쿠키의 refresh 값(없어도 멱등 처리)
                    - 출력: 204 No Content + Set-Cookie로 refresh 쿠키 만료
                    """,
            parameters = {
                    @Parameter(
                            name = "refresh",
                            in = ParameterIn.COOKIE,
                            required = false,
                            description = "Refresh Token 쿠키(없어도 멱등 처리)",
                            example = "eyJhbGciOiJIUzI1NiJ9..."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "로그아웃 성공(본문 없음)",
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "refresh 쿠키 만료 처리",
                                            schema = @Schema(type = "string", example = "refresh=; Path=/; Max-Age=0; HttpOnly;")
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String cookie = cookieUtil.readCookie(request);
        authService.logout(cookie);

        response.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.removeCookie());
        return ResponseEntity.noContent().build();
    }
}
