package com.sunlifemalaysia.app4share.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sunlifemalaysia.app4share.exception.PasswordException;
import com.sunlifemalaysia.app4share.model.dto.PasswordDto;
import com.sunlifemalaysia.app4share.service.UserAccountService;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsPasswordService userDetailsPasswordService;

    public UserAccountServiceImpl(PasswordEncoder passwordEncoder,
            UserDetailsPasswordService userDetailsPasswordService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsPasswordService = userDetailsPasswordService;
    }

    @Override
    public UserDetails changePassword(UserDetails userDetails, PasswordDto passwordDto) throws PasswordException {

        if (!passwordDto.newPassword().equals(passwordDto.confirmPassword())) {
            throw new PasswordException("New password is not equals to confirm password.");
        }

        if (!passwordEncoder.matches(passwordDto.password(), userDetails.getPassword())) {
            throw new PasswordException("Invalid password");
        }

        if (passwordEncoder.matches(passwordDto.newPassword(), userDetails.getPassword())) {
            throw new PasswordException("New password must be different from current password");
        }

        return userDetailsPasswordService.updatePassword(userDetails,
                passwordEncoder.encode(passwordDto.newPassword()));

    }

}
