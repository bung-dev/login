package project.member.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import project.member.domain.Member;
import project.member.domain.Role;

public record MemberResponse(@NotEmpty String loginId,
                             @NotEmpty String name,
                             @NotEmpty Role role) {

    public static MemberResponse from(Member member){ //Entity -> DTO
        return new MemberResponse(member.getLoginId(),
                member.getName(),
                member.getRole());
    }
}
