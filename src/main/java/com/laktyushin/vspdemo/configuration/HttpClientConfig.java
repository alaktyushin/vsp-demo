package com.laktyushin.vspdemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    public final String EXTERNAL_URL = "https://example.com";
    public final String HEADER = "Accept";
    public final String HEADER_VALUES = "application/json";

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(EXTERNAL_URL)
                .defaultHeader(HEADER, HEADER_VALUES)
                .build();
    }
}
