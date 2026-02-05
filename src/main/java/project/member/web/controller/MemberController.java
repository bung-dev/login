package project.member.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;
import project.member.domain.dto.PasswordChangeRequest;
import project.member.security.CustomMemberDetails;
import project.member.service.MemberService;

import java.util.List;

@Tag(name = "Member API", description = "회원 조회/수정/탈퇴 등 멤버 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "내 정보 조회",
            description = """
                    현재 로그인한 사용자의 정보를 조회한다.
                    
                    - 인증: 필요 (Access Token)
                    - 방식: Authorization 헤더의 Bearer Access Token으로 인증한다.
                    - 참고: 소프트 딜리트(deletedAt)된 사용자는 조회 대상에서 제외된다.
                    """,
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Bearer Access Token",
                            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않은 요청 or 토큰 만료, 토큰 불일치"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me(@AuthenticationPrincipal CustomMemberDetails member) {

        return ResponseEntity.ok(memberService.get(member.getMemberId()));
    }

    @Operation(
            summary = "모든 멤버 조회",
            description = """
                    모든 멤버 목록을 조회한다.

                    - 인증: 필요 (관리자 전용)
                    - 권한: ROLE_ADMIN
                    """,
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Bearer Access Token",
                            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료/불일치 등)"),
                    @ApiResponse(responseCode = "403", description = "권한 없음(ROLE_ADMIN 필요)")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<MemberResponse>> getAll() {
        return ResponseEntity.ok(memberService.list());
    }

    @Operation(
            summary = "회원가입",
            description = """
                    신규 멤버를 생성한다.

                    - 인증: 불필요
                    - 처리: 비밀번호는 해시 후 저장한다.
                    - 정책: loginId 등 중복 값은 가입 실패 처리한다(프로젝트 정책에 따름).
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "회원가입 요청 DTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "요청값 검증 실패(형식/필수값 누락 등)"),
                    @ApiResponse(responseCode = "409", description = "중복 회원(예: loginId 중복)")
            }
    )
    @PostMapping
    public ResponseEntity<MemberResponse> join(@RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.join(request));
    }

    @Operation(
            summary = "내 정보 변경",
            description = """
                    현재 로그인한 사용자의 프로필 정보를 수정한다.

                    - 인증: 필요 (Access Token)
                    - 범위: 닉네임/이름 등 프로필 필드 수정 (요청 DTO 기준)
                    - 참고: 비밀번호 변경은 별도 엔드포인트(/me/password)를 사용한다.
                    """,
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Bearer Access Token",
                            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "프로필 변경 요청 DTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
                    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료/불일치 등)"),
                    @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음(soft-delete 포함)")
            }
    )
    @PatchMapping("/me")
    public ResponseEntity<MemberResponse> updateMe(@AuthenticationPrincipal CustomMemberDetails member,
                                                   @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberService.update(member.getMemberId(), request));
    }

    @Operation(
            summary = "비밀번호 변경",
            description = """
                    현재 로그인한 사용자의 비밀번호를 변경한다.

                    - 인증: 필요 (Access Token)
                    - 처리: (정책에 따라) 기존 비밀번호 검증 후 새 비밀번호를 해시하여 저장한다.
                    - 응답: 성공 시 204 No Content
                    """,
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Bearer Access Token",
                            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "비밀번호 변경 요청 DTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PasswordChangeRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "변경 성공(본문 없음)"),
                    @ApiResponse(responseCode = "400", description = "요청값 검증 실패 / 비밀번호 정책 위반"),
                    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료/불일치 등)"),
                    @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음(soft-delete 포함)"),
                    @ApiResponse(responseCode = "409", description = "비밀번호 변경 불가(예: 기존 비밀번호 불일치")
            }
    )
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePasswordMe(@AuthenticationPrincipal CustomMemberDetails member,
                                                 @RequestBody PasswordChangeRequest request) {
        memberService.changePassword(member.getMemberId(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "회원 탈퇴",
            description = """
                    현재 로그인한 사용자를 탈퇴 처리한다.

                    - 인증: 필요 (Access Token)
                    - 방식: 소프트 딜리트(예: deletedAt 세팅)로 논리 삭제한다.
                    - 응답: 성공 시 204 No Content
                    """,
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = true,
                            description = "Bearer Access Token",
                            example = "Bearer eyJhbGciOiJIUzI1NiJ9..."
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "탈퇴 성공(본문 없음)"),
                    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료/불일치 등)"),
                    @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없음(soft-delete 포함)")
            }
    )
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomMemberDetails member) {
        memberService.delete(member.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
