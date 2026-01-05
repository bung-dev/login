package project.member;

import jakarta.validation.constraints.NotEmpty;

public record MemberResponse(@NotEmpty String loginId,
                             @NotEmpty String password,
                             @NotEmpty String name) {
}
