package com.example.eaimessage.config;

import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TsidFactoryConfig {

    @Bean
    public TsidFactory tsidFactory() {
        return TsidFactory.builder().build();
    }
}
