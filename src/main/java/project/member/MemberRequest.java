package project.member;

import jakarta.validation.constraints.NotEmpty;

public record MemberRequest(@NotEmpty String loginId,
                            @NotEmpty String password,
                            @NotEmpty String name) {

    public static MemberRequest from(Member member){ //Entity -> DTO
        return new MemberRequest(member.getLoginId(),
                member.getPassword(),
                member.getName());
    }
}
