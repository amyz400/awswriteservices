package com.amyz.projects.aws;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.amyz.projects.aws"})
@ComponentScan
public class AWSApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(AWSApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception { }
}
