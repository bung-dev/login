# Login-Step-by-Step-Project
<img width="800" height="500" alt="image" src="https://github.com/user-attachments/assets/bbaf10c2-26ca-4ebc-a5c2-66a67b7b8289" />


## Status (Last updated: 2026-01-17)
- ✅ Cookie 로그인 구현
- ✅ Session 로그인 구현
- ✅ Interceptor / ArgumentResolver 기반 로그인 주입(@Login) + 예외 공통 처리 + Bean Validation
- ✅ JPA Refactoring 완료
  - Entity/Repository 중심으로 영속 계층 정리
  - 트랜잭션 경계 정리(@Transactional)
  - 
- ✅ Soft Delete(논리 삭제) 적용
  - delete 시점에 물리 삭제 대신 UPDATE로 처리
  - 기본 조회에서 삭제 데이터 제외(전역 필터)
  - 
- ✅ Legacy 코드 격리 (member 스캔 범위로 legacy 제외)
- ✅ ErrorResponse.of(...) 팩토리 메서드
- ✅ Spring Security 401/403 예외 JSON 응답 처리(EntryPoint/DeniedHandler)
- ✅ CustomUserDetails/Service 구현 + @AuthenticationPrincipal 기반 구현

- ⏳ 권한 정책 정리(permitAll / role 기반 접근 제어) 마무리
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

## 4. JPA Refactoring + Soft Delete

### 4-1. JPA Refactoring 

#### 왜 바꿨는가
- (이전) 간단한 저장 방식(메모리/Map 기반)은 학습에는 좋지만, 기능이 늘어나면 조회 조건/동시성/트랜잭션 같은 운영 이슈를 다루기 어렵다
- (목표) “데이터는 DB, 비즈니스는 서비스, 접근은 Repository”로 책임을 분리해 확장 가능한 형태로 정리한다

#### 무엇을 바꿨는가
- Entity 중심으로 도메인 모델을 정리하고, Repository(JpaRepository)로 데이터 접근을 일원화한다
- Service 계층에서 트랜잭션 경계를 명확히 한다
  - 쓰기 로직: @Transactional
  - 조회 로직: @Transactional(readOnly = true)
- 리팩토링 이후 동작이 깨지지 않도록 테스트를 추가한다

#### 얻은 것 / 트레이드오프
- 장점: 조회/저장 로직이 표준화되고, “수정/삭제/조회”가 기능 단위로 예측 가능해진다
- 단점: 영속성 컨텍스트/지연 로딩/트랜잭션 범위 같은 JPA 특성을 이해하고 설계에 반영해야 한다

---

### 4-2. Soft Delete(논리 삭제) 적용 

#### 왜 넣었는가
- 운영에서는 “실수로 삭제”가 빈번하고, 삭제 이력/복구 요구가 생긴다
- 물리 삭제는 FK/이력(Audit)/장애 대응 관점에서 리스크가 커진다

#### 무엇을 했는가(핵심 아이디어)
- delete 요청이 와도 실제 DELETE 대신 deletedAt 값을 업데이트한다
- 기본 조회에서는 삭제 데이터가 자동으로 제외되도록 “전역 조건”을 적용한다
- 관리자/복구처럼 “삭제 포함 조회”가 필요한 케이스는 별도 쿼리로 분리한다

---

## 5. Spring Security 적용 + Member API (WIP)

### 5-1. Legacy 코드 격리
- 세션/인터셉터/ArgumentResolver 방식은 legacy 패키지로 묶고,
  컴포넌트 스캔 범위를 member로 제한해 현재 런타임에서 제외했다.

### 5-2. 에러 응답 생성 통일
- ErrorResponse.of(...) 정적 팩토리를 도입해
  GlobalExceptionHandler에서 new 생성 대신 of 생성자로 응답 생성 로직을 통일했다.

### 5-3. Security 401/403 JSON 응답 + 인증 주체(PK) 처리
- 401: RestAuthenticationEntryPoint, 403: RestAccessDeniedHandler 구현으로 보안 예외도 JSON으로 표준화했다.
- CustomMemberDetails에 memberId(PK) 접근 메서드를 추가하고,
  @AuthenticationPrincipal로 주입받아 내 정보 조회/수정/삭제를 PK 기반으로 처리한다.
  > 인증 주체 식별은 loginId/email 대신 PK(memberId)로 통일했다.
  > 외부 식별자는 변경/중복/정규화 이슈가 발생하기 쉬워 운영에서 정합성을 흔들 수 있기 때문이다.


## Current Focus (WIP)

- 연결 후 동작 검증 및 예외 응답(JSON) 포맷 점검
- 권한 정책 확정 (예: /members/all은 ADMIN 전용)

다음 단계로 JWT(Access/Refresh) 발급/재발급/로그아웃을 구현한 뒤,
OAuth2 Login + JWT 통합까지 확장할 예정이다.


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

## Tech Stack

- Language: Java
- Framework: Spring Boot
- Persistence: Spring Data JPA
- Security: Spring Security (WIP), JWT (Planned), OAuth2 (Planned)
- Database: MySQL, H2
- Build Tool: Gradle
- Test: JUnit

