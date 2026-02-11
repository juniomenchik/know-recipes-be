package com.aincrad.know_recipes_be.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvDebugConfig {

    @Bean
    CommandLineRunner printEnv() {
        return args -> {
            System.out.println("===== ENV VARIABLES =====");
            System.getenv().forEach((k,v) ->
                System.out.println(k + "=" + v)
            );
        };
    }
}
