package project.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.member.domain.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByLoginIdAndDeletedAtIsNull(String loginId);

    Optional<Member> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByLoginId(String loginId);
}
