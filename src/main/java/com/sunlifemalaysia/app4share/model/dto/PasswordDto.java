package com.sunlifemalaysia.app4share.model.dto;

import jakarta.validation.constraints.NotEmpty;

public record PasswordDto(@NotEmpty String password, @NotEmpty String newPassword, @NotEmpty String confirmPassword) {

}
