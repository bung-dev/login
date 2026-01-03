package project.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryTest {

    MemberRepository memberRepository = new MemberRepository();


    @AfterEach
    void afterEach(){
        memberRepository.clear();
    }

    @Test
    void save(){
        //given
        MemberRequest memberRequest = new MemberRequest("test1","test!","test1");
        //when
        Member member = memberRepository.save(memberRequest);
        //then
        Member findMember = memberRepository.findById(member.getId());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void findAll(){
        //given
        MemberRequest memberRequest = new MemberRequest("test1","test!","test1");
        MemberRequest memberRequestTwo = new MemberRequest("test2","test!","test2");

        memberRepository.save(memberRequest);
        memberRepository.save(memberRequestTwo);
        //when
        List<Member> all = memberRepository.findAll();

        //then
        assertThat(all.size()).isEqualTo(2);
        assertThat(all.get(0).getName()).isEqualTo(memberRequest.name());
        assertThat(all.get(1).getName()).isEqualTo(memberRequestTwo.name());
    }
}
