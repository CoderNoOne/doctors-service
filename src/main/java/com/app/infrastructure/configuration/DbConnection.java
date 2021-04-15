package com.app.infrastructure.configuration;

import org.hibernate.reactive.stage.Stage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static javax.persistence.Persistence.createEntityManagerFactory;

@Configuration
public class DbConnection {

    @Bean("sessionFactory")
    public Stage.SessionFactory initSessionFactory() {
        return createEntityManagerFactory("pers")
                .unwrap(Stage.SessionFactory.class);
    }


}
