package com.isudha.notify;

import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PixolNotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixolNotifyApplication.class, args);
    }

}
