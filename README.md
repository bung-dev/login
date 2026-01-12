# Login

## 구현 내용
1. Cookie 로그인 구현  
2. Session 로그인 구현  
3. 기타 기능 구현  

---

## 📌 커밋 컨벤션

| 이모지 | 태그 | 설명 |
|------|------|------|
| ✨ | feat | 기능 추가 |
| 🐛 | fix | 버그 수정 |
| 📝 | docs | 문서 수정 |
| 💄 | style | 포맷 수정 (기능 변경 없음) |
| ♻️ | refactor | 구조 개선 |
| ✅ | test | 테스트 코드 |
| 🔧 | chore | 설정 및 기타 작업 |

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

## 3. 기타 기능

### Custom Exception
- 예외를 한 곳에서 관리
- 응답 포맷 통일

### Interceptor / ArgumentResolver
- 로그인 여부 확인을 위해 `LoginCheckInterceptor` 사용
- `@Login` 커스텀 어노테이션 구현

### Bean Validation
- 요청 값 검증을 위해 Bean Validation 적용

---

## 고민한 부분

### @Login 과 HandlerMethodArgumentResolver

<img width="774" height="152" alt="image" src="https://github.com/user-attachments/assets/44edb9ac-593f-4d46-80f0-ed20702f3f72" />

컨트롤러에서 `HttpSession`을 직접 조회하지 않고  
`@Login` 어노테이션을 통해 로그인 정보를 주입받도록 구현했다.

- 단순 로그인 확인은 세션에 저장된 DTO 사용
- 매 요청마다 엔티티를 조회하지 않아 성능상 이점이 있음

다만,
- 회원 정보 변경이나 결제 같은 중요한 로직에서는
- 세션 DTO의 정보가 최신이 아닐 수 있음

그래서
- 단순 조회 → 세션 DTO 사용
- 중요한 비즈니스 로직 → 최신 엔티티 조회

이렇게 구분해서 사용하는 게 적절하다고 생각
