package com.br.Lojas_SR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.br.Lojas_SR.Repository")
@EntityScan(basePackages = "com.br.Lojas_SR.Entity")
public class LojasSrApplication {

	public static void main(String[] args) {
		SpringApplication.run(LojasSrApplication.class, args);
	}

}
