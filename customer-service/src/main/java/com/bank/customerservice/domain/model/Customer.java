package com.bank.customerservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Customer {

    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String cpf;
    private Instant createdAt;

    /**
     * The unique identifier for the customer. This will be the partition key in DynamoDB.
     * The @DynamoDbPartitionKey annotation MUST be on the public getter method.
     */
    @DynamoDbPartitionKey
    public String getCustomerId() {
        return this.customerId;
    }

    // No need to write setCustomerId() or any other getters/setters.
    // Lombok's @Data annotation will handle all the others that are not explicitly written.
}
