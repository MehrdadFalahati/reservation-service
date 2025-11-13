package com.github.mehrdadfalahati.reservation.service.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.github.mehrdadfalahati.reservation.service.dataacces")
@EnableJpaRepositories(basePackages = "com.github.mehrdadfalahati.reservation.service.dataacces")
@SpringBootApplication(scanBasePackages = "com.github.mehrdadfalahati.reservation.service")
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}
