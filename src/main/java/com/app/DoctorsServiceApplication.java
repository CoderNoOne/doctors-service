package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

@SpringBootApplication
public class DoctorsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorsServiceApplication.class, args);

    }

    @Bean
    public DefaultDataBufferFactory dataBufferFactory() {
        return new DefaultDataBufferFactory();
    }
}
