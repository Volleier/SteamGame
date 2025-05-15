package com.steamgame.steamtest.testcase;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorld {

    @Bean
    public CommandLineRunner printHelloWorld() {
        return args -> {
            System.out.println("hello world!");
        };
    }
}