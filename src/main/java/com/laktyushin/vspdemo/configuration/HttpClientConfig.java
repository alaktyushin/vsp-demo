package com.laktyushin.vspdemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    public static final String EXTERNAL_URL = "https://example.com";
    public static final String HEADER = "Accept";
    public static final String HEADER_VALUES = "application/json";

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(EXTERNAL_URL)
                .defaultHeader(HEADER, HEADER_VALUES)
                .build();
    }
}
