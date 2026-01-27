# Login-Step-by-Step-Project
<img width="800" height="500" alt="image" src="https://github.com/user-attachments/assets/bbaf10c2-26ca-4ebc-a5c2-66a67b7b8289" />

## 🚀 Login-Step-by-Step-Project Roadmap
Cookie(Login) & Session(Login)구현은 **legacy 패키지에 보관**하고,  
현재 운영 흐름은 **JPA(+Soft Delete) → Spring Security → JWT(Access/Refresh) → OAuth2(Google)** 로 확장/통합했다.


## Status (Last updated: 2026-01-27)

- ✅ Cookie 로그인 구현
- ✅ Session 로그인 구현
- ✅ Interceptor / ArgumentResolver 기반 로그인 주입(@Login) + 예외 공통 처리 + Bean Validation
- ✅ JPA Refactoring 완료
- ✅ Soft Delete(논리 삭제) 적용
- ✅ Legacy 코드 격리(학습 단계 구현 보관 + 현재 런타임 제외)
- ✅ ErrorResponse.of(...) 도입(에러 응답 생성 통일)
- ✅ Spring Security 401/403 예외 JSON 응답 처리(EntryPoint/DeniedHandler)
- ✅ CustomUserDetails/Service 구현 + @AuthenticationPrincipal 기반 “내 정보” 처리
- ✅ JWT 인증(Access Token only) 적용
  - ✅ LoginFilter 제거 → Controller/Service 기반 토큰 발급으로 전환
- ✅ Refresh Token 구현 완료(DB 저장 + 쿠키 전달 + 재발급)
- ✅ OAuth2(Google) 로그인 + JWT 통합 완료

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

### 5-4. JWT (Access Token only) - Spring Security 처리 흐름
- LoginFilter가 로그인 요청을 처리하고, 인증 성공 시 JWTUtil로 Access Token을 발급한다.
- 이후 요청은 JWTFilter가 Authorization: Bearer <token>을 추출해 JWTUtil로 검증한다.
- 토큰이 유효하면 CustomMemberDetailsService/CustomMemberDetails 기반으로 Authentication을 만들어 SecurityContext에 저장한다.

### 5-5. LoginFilter 제거 → Controller/Service 기반 토큰 발급 전환

- 기존에는 LoginFilter에서 인증 성공 시 토큰을 발급했지만, 현재는 AuthController → AuthService에서 발급하도록 리팩토링했다.
- /login 요청은 AuthenticationManager.authenticate(...)로 인증을 수행하고, 성공 시 JWTUtil.createToken으로 Access Token을 생성한다.
- 컨트롤러는 서비스만 의존하도록 구성해(Controller는 I/O, Service는 로직) 책임을 분리했다.
- 토큰 스펙(만료/헤더/이름)은 CommonToken으로 상수화해 일관되게 관리한다.

### 5-6. Refresh Token 구현(DB 저장 + 쿠키 + 재발급)
- Refresh Token은 DB에 저장하고, 로그인 시 Access/Refresh를 발급한 뒤 기존 토큰은 deleteAllByLoginId로 정리(계정당 1개 정책)했다.
- Refresh는 `CookieUtil`로 쿠키를 내려주고, Access는 Authorization: Bearer <token> 헤더로 전달한다.
- /reissue는 쿠키의 Refresh를 검증 후 새 Access/Refresh로 rotating 후, DB refresh/expiration 갱신 + 새 쿠키/Authorization 헤더로 응답한다.

> Troubleshooting: 계정당 1개 Refresh 정책(`deleteAllByLoginId`)으로 다중 기기 로그인 시 기존 토큰이 무효화될 수 있음 / 만료시간(expiration) 단위(초·밀리초) 불일치에 대한 오류가 발생할 수 있으니 문서화 하여 디버깅에 용이하도록 설계

### 로그아웃(멱등)
- `POST /logout`
- 쿠키가 없거나 DB에 없어도 **204**로 멱등 처리
- 처리: refresh 쿠키 읽기(null 가능) → DB 삭제 시도 → 만료 쿠키(Set-Cookie Max-Age=0) 내려줌

## 6. OAuth2(Google) 통합

### 6-1. OAuth2 로그인 흐름
- 로그인 시작: /oauth2/authorization/google
- 성공 시 SuccessHandler에서 토큰 발급 및 쿠키 세팅 수행

### 6-2. OAuth2 사용자 매핑
- provider별 응답을 추상화해서 loginId/email 등을 표준화
- OAuth2User를 프로젝트 사용자 모델에 맞게 감싸 CustomOauth2MemberDetails 인증 주체 일관성 유지


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

