package project.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.domain.Member;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;
import project.member.domain.dto.PasswordChangeRequest;
import project.member.repository.MemberRepository;
import project.member.web.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse join(MemberRequest req){
        if (memberRepository.existsByLoginId(req.loginId())) {
            throw ErrorCode.DUPLICATE_LOGIN_ID.exception();
        }

        String encodePassword = passwordEncoder.encode(req.password());
        Member member = Member.create(req.loginId(), req.name(), encodePassword);
        Member saved = memberRepository.save(member);

        return  MemberResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse get(Long id){
        Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::exception);


        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> list(){ //페이징 추가 예정
        return memberRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public MemberResponse update(Long id, MemberRequest req){
        Member member = memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::exception);

        member.changeName(req.name());

        return MemberResponse.from(member);
    }

    @Transactional
    public void delete(Long id){
        Member member = memberRepository.findById(id)
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::exception);

        member.softDelete();
    }
}
