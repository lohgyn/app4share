package com.sunlifemalaysia.app4share.model.dto;

import jakarta.annotation.Nonnull;

public record PasswordDto(@Nonnull String password, @Nonnull String newPassword, @Nonnull String confirmPassword) {

}
