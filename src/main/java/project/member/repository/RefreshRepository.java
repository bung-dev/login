package project.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.member.domain.Refresh;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long> {
}
