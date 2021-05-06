package com.chevron.edap.gomica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ParametersConfigurer {
    @Autowired
    private Environment env;

    @Bean
    public ConfigurationProvider getConfigurationProvider() {
        return new ConfigurationProvider(env);
    }

}
