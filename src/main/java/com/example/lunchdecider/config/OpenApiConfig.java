package com.example.lunchdecider.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lunchDeciderOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lunch Decider API")
                        .description("API documentation for Lunch Decider application")
                        .version("1.0"));
    }
}
