package com.sunlifemalaysia.app4share.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

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

		RequestMatcher[] matchers = new RequestMatcher[PROTECTED_RESOURCES.length];

		for(int i = 0; i < PROTECTED_RESOURCES.length; i ++) {
			matchers[i] = new AntPathRequestMatcher(PROTECTED_RESOURCES[i]);
		}

		http.authorizeHttpRequests(requests -> requests
				.requestMatchers(matchers).authenticated()
				.anyRequest().permitAll())
				.formLogin(login -> login.loginPage(SIGN_IN_PAGE).loginProcessingUrl(SIGN_IN_PAGE))
				.logout(logout -> logout.logoutUrl(SIGN_OUT_PAGE).logoutSuccessUrl("/"));

		return http.build();
	}

}
