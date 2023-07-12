package com.sunlifemalaysia.app4share.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SpringSecurityConfiguration {

	private static final String SIGN_IN_PAGE = "/sign-in";
	private static final String SIGN_OUT_PAGE = "/sign-out";

	private static final String[] PROTECTED_RESOURCES = { "/profile" };

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(requests -> requests
				.requestMatchers(PROTECTED_RESOURCES).authenticated()
				.anyRequest().permitAll())
				.formLogin(login -> login.loginPage(SIGN_IN_PAGE).loginProcessingUrl(SIGN_IN_PAGE))
				.logout(logout -> logout.logoutUrl(SIGN_OUT_PAGE).logoutSuccessUrl("/"));

		return http.build();
	}

}
