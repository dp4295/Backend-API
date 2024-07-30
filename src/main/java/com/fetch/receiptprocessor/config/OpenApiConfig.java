package com.fetch.receiptprocessor.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {

    @Bean
    public GroupedOpenApi customOpenApi() {
        return GroupedOpenApi.builder()
                .group("receipt-processor")
                .pathsToMatch("/receipts/**")
                .build();
    }
}
