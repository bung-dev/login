package project.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId;

    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreatedDate
    private LocalDateTime createdAt;

    private Boolean deletedAt;

    @Builder(access = AccessLevel.PROTECTED)
    private Member(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.role = Role.ROLE_Member;
    }

    public static Member create(String loginId, String name, String password) {
        return Member.builder()
                .loginId(loginId)
                .name(name)
                .password(password)
                .build();
    }

    public boolean delete() {
        return this.deletedAt != null;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
