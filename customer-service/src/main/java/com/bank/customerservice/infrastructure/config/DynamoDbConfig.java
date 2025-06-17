package com.bank.customerservice.infrastructure.config;

import com.bank.customerservice.domain.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    private static final String TABLE_NAME = "customers";

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(
            @Value("${spring.cloud.aws.region.static:sa-east-1}") String region,
            @Value("${spring.cloud.aws.credentials.access-key:test}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key:test}") String secretKey,
            @Value("${spring.cloud.aws.endpoint:http://localhost:4566}") String endpoint
    ) {
        // Explicitly create the underlying HTTP client
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                // You can add further configuration here, like timeouts
                .build();

        return DynamoDbAsyncClient.builder()
                .httpClient(httpClient) // <-- Set the explicit HTTP client
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                )
                .endpointOverride(URI.create(endpoint))
                .build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
    }

    @Bean
    public DynamoDbAsyncTable<Customer> customerDynamoDbAsyncTable(DynamoDbEnhancedAsyncClient enhancedAsyncClient) {
        return enhancedAsyncClient.table(TABLE_NAME, TableSchema.fromBean(Customer.class));
    }
}
