package project.member;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Repository
public class MemberRepository {

    private final Map<Long,Member> store = new HashMap<>();
    private static long sequence = 0L;

    public Member save(MemberRequest memberRequest){
        Member member = new Member();
        member.setLoginId(memberRequest.loginId());
        member.setPassword(memberRequest.password());
        member.setName(memberRequest.name());
        member.setId(++sequence);
        store.put(member.getId(),member);

        return member;
    }

    public Member findById(Long id){

        return store.get(id);
    }

    public List<Member> findAll(){

        return new ArrayList<>(store.values());
    }

    public void deleteMemberById(Long id){
        store.remove(id);
    }

    public void clear(){
        store.clear();
    }
}
