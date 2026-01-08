package project.member.web.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.member.domain.dto.MemberResponse;
import project.member.web.argumentresolver.Login;

import java.util.Map;
@Log4j2
@RestController
public class AuthController {

    @GetMapping("/")
    public ResponseEntity<Map<String , Object>> checkLogin(@Login MemberResponse loginMember) {
        if (loginMember == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of("message", "로그인이 필요합니다", "authenticated", false)
                    );
        }
        return ResponseEntity.ok(
                Map.of("message", "로그인되었습니다", "authenticated", true)
        );
    }
}
