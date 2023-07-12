package com.sunlifemalaysia.app4share.service.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sunlifemalaysia.app4share.model.auth.UserAccount;
import com.sunlifemalaysia.app4share.repository.UserAccountRepository;

@Service
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {

    private final UserAccountRepository userAccountRepository;

    public UserDetailsPasswordServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {

        UserAccount userAccount = userAccountRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        userAccount.setPassword(newPassword);

        userAccountRepository.save(userAccount);

        return userAccount;

    }

}
