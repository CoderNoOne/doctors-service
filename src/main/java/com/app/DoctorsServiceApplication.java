package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

class A {

    public static void main(String[] args) {

//        final List<Integer> integers = List.of(1, 2, 3)
//                .stream()
//                .collect(Collectors.toList());
//
//
//        final List<?> collect = Stream.of(1, 2, 3, 10, 5, 16)
//                .mapMulti((value, consumer) -> {
//                    System.out.println("Value: " + value);
//                    consumer.accept(++value);
//                })
//                .collect(Collectors.toList());


        final List<Object> collect1 = Stream.of(List.of(1, 2, 3), List.of(4, 5, 6))
                .mapMulti((list, consumer) -> {
                    System.out.println(list);
                    list.forEach(consumer);
                })
                .collect(Collectors.toList());

        System.out.println(collect1);


    }

}

