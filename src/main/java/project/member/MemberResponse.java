package project.member;

import jakarta.validation.constraints.NotEmpty;

public record MemberResponse(@NotEmpty String loginId,
                             @NotEmpty String password,
                             @NotEmpty String name) {

    public static MemberResponse from(Member member){ //Entity -> DTO
        return new MemberResponse(member.getLoginId(),
                member.getPassword(),
                member.getName());
    }
}
