package com.example.eaimessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EaiMessageModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EaiMessageModuleApplication.class, args);
    }
}
