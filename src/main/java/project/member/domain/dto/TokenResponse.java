package project.member.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(@NotBlank String accessToken,
                            @NotBlank String tokenType) {
    public static TokenResponse from(String token,String tokenType) {
        return new TokenResponse(token, tokenType);
    }
}