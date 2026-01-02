package project.member;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Member getMemberById(@PathVariable Long id) {
        return memberRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Member CreateMember(@RequestBody @Valid MemberRequest request){

        return memberRepository.save(request);
    }
}
