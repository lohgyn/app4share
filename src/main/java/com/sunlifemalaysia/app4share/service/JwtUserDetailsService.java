package com.sunlifemalaysia.app4share.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    public static final String ADMIN = "ADMIN";
    public static final String ROLE_ADMIN = "ROLE_" + ADMIN;
    private final List<String> users;

    public JwtUserDetailsService(@Value("${app.auth.users}") final List<String> users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        if (!this.users.contains(username)) {
            throw new UsernameNotFoundException("User name not found");
        }

        return new User(username, username, Collections.singletonList(new SimpleGrantedAuthority(ROLE_ADMIN)));
    }

}
