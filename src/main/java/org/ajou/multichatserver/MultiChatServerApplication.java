package org.ajou.multichatserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MultiChatServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiChatServerApplication.class, args);
    }

}
