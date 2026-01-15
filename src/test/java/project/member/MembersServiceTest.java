package project.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import project.member.repository.InMemoryMemberRepository;
import project.member.service.InMemoryMemberService;
import project.member.domain.dto.MemberRequest;
import project.member.domain.dto.MemberResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class MembersServiceTest {

    InMemoryMemberRepository inMemoryMemberRepository = new InMemoryMemberRepository();

    InMemoryMemberService inMemoryMemberService = new InMemoryMemberService(inMemoryMemberRepository);

    @AfterEach
    void tearDown() {
        inMemoryMemberRepository.clear();
    }

    @Test
    void createMemberAndGetMemberById(){
        // given
        MemberRequest memberRequest = new MemberRequest("test1", "test!", "test1");
        // when
        MemberResponse memberResponse = inMemoryMemberService.createMember(memberRequest);
        MemberResponse getMemberByIdResponse = inMemoryMemberService.getMemberById(1L);
        // then
        assertThat(memberResponse.name()).isEqualTo(getMemberByIdResponse.name());

    }

    @Test
    void getAllMembers(){
        //given
        MemberRequest memberRequest1 = new MemberRequest("test1", "test!", "test1");
        MemberRequest memberRequest2 = new MemberRequest("test2", "test!", "test2");

        MemberResponse member1 = inMemoryMemberService.createMember(memberRequest1);
        MemberResponse member2 = inMemoryMemberService.createMember(memberRequest2);

        //when
        List<MemberResponse> allMembers = inMemoryMemberService.getAllMembers();

        //then
        assertThat(allMembers.size()).isEqualTo(2);
        assertThat(allMembers.get(0).name()).isEqualTo(member1.name());
        assertThat(allMembers.get(1).name()).isEqualTo(member2.name());
    }

    @Test
    void deleteByMember(){
        //given
        MemberRequest memberRequest = new MemberRequest("test1", "test!", "test1");

        inMemoryMemberService.createMember(memberRequest);
        //when
        inMemoryMemberService.deleteMember(1L);
        //then
        MemberResponse deletedMember = inMemoryMemberService.getMemberById(1L);
        assertThat(deletedMember.name()).isEqualTo("deletedName");
    }
}
