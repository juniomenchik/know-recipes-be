package com.aincrad.know_recipes_be;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MainTest {
    public static void main(String[] args) {
    var res = new BCryptPasswordEncoder().encode("password123");
        System.out.println(res);
    }
}
