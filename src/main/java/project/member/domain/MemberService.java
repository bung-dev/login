package project.member.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;
import project.member.web.exception.CustomException;
import project.member.web.exception.ErrorCode;

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
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        } //findById를 Optional이면 orElseThorw로 리팩토링이 가능, JPA로 리팩토링하며 구현예정

        return new MemberResponse(getMember.getLoginId(),
                getMember.getName());
    }

    public MemberResponse login(String loginId, String password){
        return memberRepository.findByLoginId(loginId)
                .filter(member -> !(member.getStatus() == MemberStatus.DELETED))
                .filter(member -> member.getPassword().equals(password))
                .map(MemberResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));
    }

    public List<MemberResponse> getAllMembers(){
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    public void deleteMember(Long id){
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
            //findById를 Optional이면 orElseThorw로 리팩토링이 가능, JPA로 리팩토링하며 구현예정
        } else {
            memberRepository.deleteMemberById(id);
        }
    }
}
