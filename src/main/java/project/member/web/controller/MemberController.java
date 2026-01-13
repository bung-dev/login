package project.member.web.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.member.domain.MemberService;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;
import project.member.web.SessionConst;

import java.util.List;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> createMembers(@Valid @RequestBody MemberRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MemberResponse>> getAllMembers(){
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembers(@PathVariable Long id){
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/login")
    public ResponseEntity<MemberResponse> loginSession(@RequestParam String loginId,
                                                       @RequestParam String password,
                                                       HttpServletRequest request){
        MemberResponse login = memberService.login(loginId, password);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, login);

        return  ResponseEntity.ok().body(login);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutSession(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 쿠키 로그인 방식, 세션 로그인 방식을 주로 사용하기에 주석처리
     */

    //    @PostMapping("/login/cookie")
//    public ResponseEntity<MemberResponse> loginCookie(@RequestParam String loginId, @RequestParam String password){
//        MemberResponse login = memberService.login(loginId, password);
//        ResponseCookie cookie = getResponseCookie(login);
//
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(login);
//    }

//    @PostMapping("/logout/cookie")
//    public ResponseEntity<Void> logoutCookie(@CookieValue(name = "loginId",required = false) String loginId) {
//        ResponseCookie cookie = expireCookie(loginId);
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
//    }

//    private static @NonNull ResponseCookie getResponseCookie(MemberResponse login) {
//        return ResponseCookie.from("loginId", login.loginId())
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .sameSite("Strict")
//                .maxAge(3600)
//                .build();
//    }
//
//    private static ResponseCookie expireCookie(String loginId){
//        return ResponseCookie.from("loginId",loginId)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .sameSite("Strict")
//                .maxAge(0)
//                .build();
//    }
}
