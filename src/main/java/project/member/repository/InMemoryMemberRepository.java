package project.member.repository;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import project.member.domain.Member;

import java.util.*;

@Log4j2
@Repository
public class InMemoryMemberRepository {

    private final Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    public Member save(Member member){
        member.setId(++sequence);
        store.put(member.getId(),member);
        return member;
    }

    public Member findById(Long id){

        return store.get(id);
    }

    public Optional<Member> findByLoginId(String loginId){
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }


    public List<Member> findAll(){

        return new ArrayList<>(store.values());
    }

    public void deleteMemberById(Long id){
        Member member = store.get(id);
        member.setName("deletedName");
        member.setLoginId("deletedMember");
        member.setPassword("deletedPassword");
    }

    public void clear(){
        store.clear();
        sequence = 0L;
    }
}
