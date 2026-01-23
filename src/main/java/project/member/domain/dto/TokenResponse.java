package project.member.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(@NotBlank String accessToken,
                            @NotBlank String refreshToken) {
    public static TokenResponse from(String accessToken,String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}