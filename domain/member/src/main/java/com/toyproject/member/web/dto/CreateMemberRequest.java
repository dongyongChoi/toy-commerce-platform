package com.toyproject.member.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequest(
    @NotBlank(message = "name must not be blank")
    String name,
    @Email(message = "email must be a valid email address")
    @NotBlank(message = "email must not be blank")
    String email
) {
}

