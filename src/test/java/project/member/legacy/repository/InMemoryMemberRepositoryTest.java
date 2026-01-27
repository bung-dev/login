package project.member.legacy.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import project.member.domain.Member;
import project.legacy.repositroy.InMemoryMemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class InMemoryMemberRepositoryTest {

    InMemoryMemberRepository inMemoryMemberRepository = new InMemoryMemberRepository();


    @AfterEach
    void afterEach(){
        inMemoryMemberRepository.clear();
    }

    @Test
    void saveAndFindById(){
        //given
        Member member1 = Member.builder()
                .loginId("test1")
                .password("test1")
                .name("test1")
                .build();
        //when
        Member findMember = inMemoryMemberRepository.save(member1);
        //then
        assertThat(findMember).isEqualTo(member1);
}
    @Test
    void findAll(){
        //given

        Member member1 = Member.builder()
                .loginId("test1")
                .password("test1")
                .name("test1")
                .build();
        Member member2 = Member.builder()
                .loginId("test2")
                .password("test2")
                .name("test2")
                .build();

        inMemoryMemberRepository.save(member1);
        inMemoryMemberRepository.save(member2);
        //when
        List<Member> all = inMemoryMemberRepository.findAll();

        //then
        assertThat(all.size()).isEqualTo(2);
        assertThat(all.get(0).getName()).isEqualTo(member1.getName());
        assertThat(all.get(1).getName()).isEqualTo(member2.getName());
    }

    @Test
    void deleteByMemberId(){
        //given
        Member member1 = Member.builder()
                .loginId("test1")
                .password("test1")
                .name("test1")
                .build();

        Member member = inMemoryMemberRepository.save(member1);
        //when
        System.out.println(member.toString());
        inMemoryMemberRepository.deleteMemberById(member.getId());

        //then
        Member deletedMember = inMemoryMemberRepository.findById(member.getId());
        assertThat(deletedMember.getName()).isEqualTo("deletedName");
    }
}
