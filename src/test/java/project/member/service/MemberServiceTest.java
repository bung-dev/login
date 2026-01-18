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

    @Test
    void list() {
        //given
        MemberRequest memberRequest1 = new MemberRequest("testtest1", "testtest1!", "테스트1");
        MemberRequest memberRequest2 = new MemberRequest("testtest2", "testtest2!", "테스트2");
        memberService.join(memberRequest1);
        memberService.join(memberRequest2);
        //when
        List<MemberResponse> listMember = memberService.list();
        //then
        assertThat(listMember).hasSize(2);
        assertThat(listMember.get(0).name()).isEqualTo(memberRequest1.name());
        assertThat(listMember.get(1).name()).isEqualTo(memberRequest2.name());
    }


}
