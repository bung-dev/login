package project;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import project.member.MemberRequest;
import project.member.MemberService;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberService memberService;

    @Override
    public void run(@NonNull ApplicationArguments args) throws Exception {
        memberService.createMember(new MemberRequest("test1", "test!", "test1"));
        memberService.createMember(new MemberRequest("test2", "test!", "test2"));
    }
}
