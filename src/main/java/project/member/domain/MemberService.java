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
        }

        return new MemberResponse(getMember.getLoginId(),
                getMember.getName());
    }

    public MemberResponse login(String loginId, String password){
        return memberRepository.findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password))
                .map(MemberResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 맞지 않습니다"));
        // 예외 처리 로직 추가해야함
        // 1. @ControllerAdvice 를 통해 전역 예외 처리 -> 로그인 실패 예외
        // 2. httpStatus 코드 및 body 응답 추가
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
