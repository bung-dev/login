# Login-Step-by-Step-Project
<img width="800" height="500" alt="image" src="https://github.com/user-attachments/assets/bbaf10c2-26ca-4ebc-a5c2-66a67b7b8289" />


## Status (Last updated: 2026-01-14)
- ✅ Cookie 로그인 구현
- ✅ Session 로그인 구현
- ✅ Interceptor / ArgumentResolver 기반 로그인 주입(@Login) + 예외 공통 처리 + Bean Validation

- 🚧 **JPA Refactoring + Spring Security 적용 (동시 진행)**
  - [ ] (JPA) Entity/Repository 중심으로 영속 계층 정리
  - [ ] (JPA) 트랜잭션 경계 정리(@Transactional) + 테스트 추가
  - [ ] (Security) SecurityFilterChain 기반 인증/인가 구성
  - [ ] (Security) 권한 정책 정리(permitAll / role 기반 접근제어)
  - [ ] (Security) PasswordEncoder(BCrypt) 적용 + 예외 처리(EntryPoint/DeniedHandler)

- ⏳ JWT 적용 (Access/Refresh, 재발급, 로그아웃)
- ⏳ OAuth2 Login + JWT 통합
---

## 1. Cookie 로그인

- 로그인 성공 시 서버가 쿠키에 사용자 정보를 담아 응답
- 클라이언트는 이후 요청마다 쿠키를 함께 전송
- 서버는 쿠키를 통해 로그인 여부를 판단

### ResponseCookie
- 스프링 프레임워크 5.0부터 지원하며 응답 헤더에 허용가능
 - ex) 매개변수로 HttpServletResponse를 받아 response.addCookie(cookie)를 하지않고 ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())가능
---

## Cookie 로그인 한계

- 쿠키에 정보가 직접 저장됨
- 탈취 및 위·변조 위험
- 보안 측면에서 한계가 있음

→ Session 로그인 방식으로 전환

---

## 2. Session 로그인

- 로그인 성공 시 서버에서 세션 생성
- 세션은 Key-Value 구조
  - Key: 세션 ID (중복되지 않는 고유한 값)
  - Value: 사용자 정보
- 세션 ID만 쿠키에 담아 응답
- 이후 요청 시 세션 ID로 인증 처리

- `HttpSession` 사용

---

## 3. Interceptor/ArgumentResolver + CustomException

### Interceptor / ArgumentResolver
- 로그인 여부 확인을 위해 `LoginCheckInterceptor` 사용
- `@Login` 커스텀 어노테이션 구현

### Custom Exception
- 예외를 한 곳에서 관리
- 응답 포맷 통일

### Bean Validation
- 요청 값 검증을 위해 Bean Validation 적용

---

## @Login + HandlerMethodArgumentResolver를 사용한 이유

<img width="774" height="152" alt="image" src="https://github.com/user-attachments/assets/44edb9ac-593f-4d46-80f0-ed20702f3f72" />

컨트롤러에서 HttpSession을 직접 조회하지 않고, @Login으로 로그인 정보를 주입받도록 구성

단순 조회는 세션 DTO 사용(불필요한 DB 조회 방지)

중요한 비즈니스 로직(정보 변경/결제 등)은 최신 엔티티 조회로 정합성 확보
→ “가벼운 요청”과 “중요한 요청”을 분리

---

## Current Focus (WIP)
**JPA 리팩토링 + Spring Security 적용을 병행 중입니다.**
- [ ] JPA 전환(Repository/Tx) + 테스트
- [ ] SecurityFilterChain/권한정책/예외처리 + BCrypt

---


## 📌 Commit Convention

| 이모지 | 태그 | 설명 |
|------|------|------|
| ✨ | feat | 기능 추가 |
| 🐛 | fix | 버그 수정 |
| 📝 | docs | 문서 수정 |
| 💄 | style | 포맷 수정 (기능 변경 없음) |
| ♻️ | refactor | 구조 개선 |
| ✅ | test | 테스트 코드 |
| 🔧 | chore | 설정 및 기타 작업 |

