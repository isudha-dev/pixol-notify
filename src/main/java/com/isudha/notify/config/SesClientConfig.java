package com.isudha.notify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class SesClientConfig {

    @Bean
    public SesClient sesClient() {
        // Create a SesClient instance with default credentials provider (e.g., from AWS_PROFILE or environment variables)
        return SesClient.builder()
                .region(Region.AP_SOUTH_1)  // Specify your AWS region
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }
}
