package com.example.eaimessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EaiMessageModuleApplication {

    private static final Logger log = LoggerFactory.getLogger(EaiMessageModuleApplication.class);

    public static void main(String[] args) {
        log.info("EAI message module 애플리케이션 시작");
        SpringApplication.run(EaiMessageModuleApplication.class, args);
    }
}
