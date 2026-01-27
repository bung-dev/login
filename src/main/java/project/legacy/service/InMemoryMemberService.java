package project.legacy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.member.domain.Member;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;
import project.legacy.repositroy.InMemoryMemberRepository;
import project.member.web.exception.CustomException;
import project.member.web.exception.ErrorCode;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InMemoryMemberService {

    private final InMemoryMemberRepository inMemoryMemberRepository;

    public MemberResponse createMember(MemberRequest req) {
        Member member = Member.builder()
                .loginId(req.loginId())
                .password(req.password())
                .name(req.name())
                .build();

        Member saveMember = inMemoryMemberRepository.save(member);

        return MemberResponse.from(saveMember);
    }

    public MemberResponse getMemberById(Long id){
        Member getMember = inMemoryMemberRepository.findById(id);
        if (getMember == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        } //findById를 Optional이면 orElseThorw로 리팩토링이 가능, JPA로 리팩토링하며 구현예정

        return new MemberResponse(getMember.getLoginId(),
                getMember.getName(),getMember.getRole());
    }

    public MemberResponse login(String loginId, String password){
        return inMemoryMemberRepository.findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password))
                .map(MemberResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));
    }

    public List<MemberResponse> getAllMembers(){
        return inMemoryMemberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    public void deleteMember(Long id){
        Member member = inMemoryMemberRepository.findById(id);
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
            //findById를 Optional이면 orElseThorw로 리팩토링이 가능, JPA로 리팩토링하며 구현예정
        } else {
            inMemoryMemberRepository.deleteMemberById(id);
        }
    }
}
