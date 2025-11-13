package com.github.mehrdadfalahati.reservation.service.dataaccess.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.github.mehrdadfalahati.reservation.service.dataaccess.repository")
@EntityScan(basePackages = "com.github.mehrdadfalahati.reservation.service.dataaccess.entity")
public class TestDataAccessConfiguration {
}
