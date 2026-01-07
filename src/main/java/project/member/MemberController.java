package project.member;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<MemberResponse> createMembers(@Valid MemberRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers(){
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembers(@PathVariable Long id){
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<MemberResponse> loginMember(@RequestParam String loginId, @RequestParam String password){
        MemberResponse login = memberService.login(loginId, password);
        ResponseCookie cookie = getResponseCookie(login);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(login);
    }


    @PostMapping("/login/session")
    public ResponseEntity<MemberResponse> loginSession(@RequestParam String loginId,
                                                       @RequestParam String password,
                                                       HttpServletRequest request){
        MemberResponse login = memberService.login(loginId, password);
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("loginId", loginId);

        return  ResponseEntity.ok().body(login);

    }

    private static @NonNull ResponseCookie getResponseCookie(MemberResponse login) {
        return ResponseCookie.from("loginId", login.loginId())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(3600)
                .build();
    }
}
