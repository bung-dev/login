package project.member.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.member.domain.Member;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;
import project.member.repository.MemberRepository;
import project.member.web.exception.CustomException;
import project.member.web.exception.ErrorCode;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;


    @Test
    void joinAndGet() {
        //given
        MemberRequest memberRequest = new MemberRequest("testtest1", "testtest1!", "테스트1");
        //when
        MemberResponse memberResponse = memberService.join(memberRequest);
        Member member = memberRepository.findByLoginId(memberResponse.loginId())
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::exception);
        //then
        MemberResponse getMember = memberService.get(member.getId());
        assertThat(getMember.name()).isEqualTo(member.getName());
    }
    
}
