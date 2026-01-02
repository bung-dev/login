package project.member;

import jakarta.validation.constraints.NotEmpty;

public record MemberRequest(@NotEmpty String loginId, @NotEmpty String password, @NotEmpty String name) {
}
