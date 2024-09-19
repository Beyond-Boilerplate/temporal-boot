package com.github.sardul3.temporal_boot.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.github.sardul3.temporal_boot.*"
})
@EntityScan(basePackages = "com.github.sardul3.temporal_boot.common.models") 
@EnableJpaRepositories(basePackages = {"com.github.sardul3.temporal_boot.*"})
@AllArgsConstructor
@Slf4j
public class TemporalBootApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TemporalBootApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
