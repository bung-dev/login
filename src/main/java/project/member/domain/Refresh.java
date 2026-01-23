package project.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refresh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String loginId;
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
