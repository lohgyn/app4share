package com.sunlifemalaysia.app4share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class App4ShareApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(App4ShareApplication.class, args);
	}

}
