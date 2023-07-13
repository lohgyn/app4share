package com.sunlifemalaysia.app4share.configuration;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.sunlifemalaysia.app4share.model.auth.Role;
import com.sunlifemalaysia.app4share.model.auth.UserAccount;
import com.sunlifemalaysia.app4share.repository.RoleRepository;
import com.sunlifemalaysia.app4share.repository.UserAccountRepository;

@Configuration
/**
 * Initialize admin user if none exists
 */
public class InitializeAdminUserConfiguration implements InitializingBean {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;
    private final String username;
    private final String secret;

    public InitializeAdminUserConfiguration(@Value("${app.auth.username}") final String username,
            @Value("${app.auth.secret}") final String secret,
            RoleRepository roleRepository,
            UserAccountRepository userAccountRepository) {
        this.roleRepository = roleRepository;
        this.userAccountRepository = userAccountRepository;
        this.username = username;
        this.secret = secret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Optional<UserAccount> userAccountOptional = userAccountRepository.findByUsername(username);

        if (userAccountOptional.isPresent()) {
            return;
        }

        Optional<Role> roleOptional = roleRepository.findByName(ROLE_ADMIN);

        final Role adminRole;
        
        if (!roleOptional.isPresent()) {
            adminRole = roleRepository.save(new Role(ROLE_ADMIN));
        } else {
            adminRole = roleOptional.get();
        }

        UserAccount userAccount = new UserAccount(username, secret, true, false);
        userAccount.setRoles(Arrays.asList(adminRole));
        userAccountRepository.save(userAccount);
    }
}
