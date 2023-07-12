package com.sunlifemalaysia.app4share.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sunlifemalaysia.app4share.exception.PasswordException;
import com.sunlifemalaysia.app4share.model.auth.UserAccount;
import com.sunlifemalaysia.app4share.model.dto.PasswordDto;
import com.sunlifemalaysia.app4share.repository.UserAccountRepository;
import com.sunlifemalaysia.app4share.service.UserAccountService;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsPasswordService userDetailsPasswordService;
    private final UserAccountRepository userAccountRepository;

    public UserAccountServiceImpl(PasswordEncoder passwordEncoder,
            UserDetailsPasswordService userDetailsPasswordService, UserAccountRepository userAccountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsPasswordService = userDetailsPasswordService;
        this.userAccountRepository = userAccountRepository;
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

        userDetails = userDetailsPasswordService.updatePassword(userDetails,
                passwordEncoder.encode(passwordDto.newPassword()));
        return userDetails;

    }

    @Override
    public UserDetails setDarkMode(UserDetails userDetails, Boolean darkMode) {

        UserAccount userAccount = userAccountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        userAccount.setDarkMode(darkMode == null ? false : darkMode);

        userAccountRepository.save(userAccount);

        ((UserAccount) userDetails).setDarkMode(userAccount.isDarkMode());
        
        return userDetails;
    }

}
