package com.chevron.edap.gomica;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBootAppRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringBootApp.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);

    }
}
