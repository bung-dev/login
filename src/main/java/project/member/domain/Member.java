package project.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId;

    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String loginId, String name, String password, Role role) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.role = role;
        this.status = MemberStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return status == MemberStatus.DELETED;
    }
}
