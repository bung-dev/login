package project.member.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.member.domain.Member;
import project.member.repository.MemberRepository;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> memberOptional = memberRepository.findByLoginIdAndDeletedAtIsNull(username);

        Member member = memberOptional.orElseThrow(() -> {
            log.info("사용자를 찾을 수 없습니다: " + username);
            return new UsernameNotFoundException("사용자를 찾을 수 없습니다" + username);
        });
        return new CustomMemberDetails(member);
    }
}
