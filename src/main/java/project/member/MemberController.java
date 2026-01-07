package project.member;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/login")
    public ResponseEntity<MemberResponse> loginMember(@RequestParam String loginId, @RequestParam String password){
        MemberResponse login = memberService.login(loginId, password);
        ResponseCookie cookie = getResponseCookie(login);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(login);
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
