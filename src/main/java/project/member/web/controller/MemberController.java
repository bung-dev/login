package project.member.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;
import project.member.security.CustomMemberDetails;
import project.member.service.MemberService;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberResponse> get(@AuthenticationPrincipal CustomMemberDetails member) {
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

    @PutMapping
    public ResponseEntity<MemberResponse> update(@AuthenticationPrincipal CustomMemberDetails member,
                                                 @RequestBody MemberRequest request){
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberService.update(member.getMemberId(), request));
    }

    @DeleteMapping
    public ResponseEntity<MemberResponse> delete(@AuthenticationPrincipal CustomMemberDetails member){
        memberService.delete(member.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
