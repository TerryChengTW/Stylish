package com.stylish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class StylishApplication {

    public static void main(String[] args) {
        SpringApplication.run(StylishApplication.class, args);
    }

    @RestController
    static class HelloController {
        @GetMapping("/")
        public String hello() {
            return "Hello World, I'm automatically deployed through GitHub Actions!";
        }
    }

}
