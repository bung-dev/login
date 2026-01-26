package project.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refresh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String loginId;

    @Column(name = "refresh", length = 512)
    private String refresh;
    private long expiration;


    private Refresh(String loginId, String refresh, long expiration) {
        this.loginId = loginId;
        this.refresh = refresh;
        this.expiration = expiration;
    }

    public static Refresh create(String loginId, String refreshToken, long expiration) {
        return new Refresh(loginId, refreshToken, expiration);
    }

    public void changeLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void changeRefresh(String refresh) {
        this.refresh = refresh;
    }

    public void changeExpiration(long expiration) {
        this.expiration = expiration;
    }
}
