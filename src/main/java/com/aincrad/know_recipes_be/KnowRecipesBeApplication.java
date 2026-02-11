package com.aincrad.know_recipes_be;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KnowRecipesBeApplication implements CommandLineRunner {


	public static void main(String[] args) {

		SpringApplication.run(KnowRecipesBeApplication.class, args);

	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println(System.getenv("DATABASE_URL"));
		System.out.println(System.getenv("SPRING_PROFILES_ACTIVE"));
	}

}
