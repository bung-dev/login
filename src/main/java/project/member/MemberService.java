package project.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member createMember = memberRepository.save(memberRequest);

        return new MemberResponse(createMember.getLoginId(),
                createMember.getPassword(),
                createMember.getName());
    }

    public MemberResponse getMemberById(Long id){
        Member getMember = memberRepository.findById(id);

        return new MemberResponse(getMember.getLoginId(),
                getMember.getPassword(),
                getMember.getName());
    }
}
