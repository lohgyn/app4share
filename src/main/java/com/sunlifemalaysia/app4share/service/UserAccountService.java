package com.sunlifemalaysia.app4share.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.sunlifemalaysia.app4share.exception.PasswordException;
import com.sunlifemalaysia.app4share.model.dto.PasswordDto;

public interface UserAccountService {

    public UserDetails changePassword(UserDetails userDetails, PasswordDto passwordDto) throws PasswordException;
}
