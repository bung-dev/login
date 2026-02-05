package project.member.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Login-Step-by-Step-Project API 목록")
                        .description("단순 쿠키부터 OAuth2까지 단계별 로그인 구현 프로젝트 API 입니다")
                        .version("0.1"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발용 서버")
                ));
    }

    @Bean
    public GroupedOpenApi groupedOpenApiV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupedOpenApiV2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/api/v2/**")
                .build();
    }
}
