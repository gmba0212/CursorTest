package com.example.eaimessage;

import com.example.eaimessage.config.EaiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EaiProperties.class)
public class EaiMessageModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EaiMessageModuleApplication.class, args);
    }
}
