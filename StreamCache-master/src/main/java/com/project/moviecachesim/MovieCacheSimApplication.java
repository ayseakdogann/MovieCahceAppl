package com.project.moviecachesim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovieCacheSimApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieCacheSimApplication.class, args);
    }

}
