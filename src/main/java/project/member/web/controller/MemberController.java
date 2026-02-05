package project.member.web.controller;

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

@Tag(name = "Member API",description = "멤버 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me(@AuthenticationPrincipal CustomMemberDetails member) {
        return ResponseEntity.ok(memberService.get(member.getMemberId()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MemberResponse>> getAll(){
        return ResponseEntity.ok(memberService.list());
    }

    @PostMapping
    public ResponseEntity<MemberResponse> join(@RequestBody MemberRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.join(request));
    }

    @PatchMapping("/me")
    public ResponseEntity<MemberResponse> updateMe(@AuthenticationPrincipal CustomMemberDetails member,
                                                 @RequestBody MemberRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberService.update(member.getMemberId(), request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePasswordMe(@AuthenticationPrincipal CustomMemberDetails member,
                                                         @RequestBody PasswordChangeRequest request){
        memberService.changePassword(member.getMemberId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomMemberDetails member){
        memberService.delete(member.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
