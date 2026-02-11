package com.aincrad.know_recipes_be;

import com.aincrad.know_recipes_be.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KnowRecipesBeApplication implements CommandLineRunner {

	@Autowired
	private RecipeService recipeService;

	public static void main(String[] args) {

		SpringApplication.run(KnowRecipesBeApplication.class, args);

	}


	@Override
	public void run(String... args) throws Exception {
//		recipeService.generateTenRecipesForUser(1L);
	}

}
