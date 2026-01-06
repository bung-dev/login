package project.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = Member.builder()
                .loginId(memberRequest.loginId())
                .password(memberRequest.password())
                .name(memberRequest.name())
                .build();

        Member saveMember = memberRepository.save(member);

        return MemberResponse.from(saveMember);
    }

    public MemberResponse getMemberById(Long id){
        Member getMember = memberRepository.findById(id);
        if (getMember == null) {
            return null; // 임시 널 반환, 예외 처리 로직 추가해야함
        }

        return new MemberResponse(getMember.getLoginId(),
                getMember.getPassword(),
                getMember.getName());
    }

    public MemberResponse login(String loginId, String password){
        return memberRepository.findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password))
                .map(MemberResponse::from)
                .orElse(null);
    }

    public List<MemberResponse> getAllMembers(){
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    public void deleteMember(Long id){
        memberRepository.deleteMemberById(id);
    }
}
