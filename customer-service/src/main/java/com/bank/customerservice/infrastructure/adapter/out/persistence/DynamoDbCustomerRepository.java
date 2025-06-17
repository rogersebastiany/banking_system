package com.bank.customerservice.infrastructure.adapter.out.persistence;

import com.bank.customerservice.application.port.out.CustomerRepository;
import com.bank.customerservice.domain.model.Customer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The concrete implementation of the CustomerRepository port for DynamoDB.
 * This is the "outgoing adapter" for our persistence layer.
 */
@Repository
public class DynamoDbCustomerRepository implements CustomerRepository {

    private final DynamoDbAsyncTable<Customer> customerTable;

    public DynamoDbCustomerRepository(@Qualifier("customerDynamoDbAsyncTable") DynamoDbAsyncTable<Customer> customerTable) {
        this.customerTable = customerTable;
    }

    @Override
    public CompletableFuture<Customer> save(Customer customer) {
        return customerTable.putItem(customer)
                .thenApply(v -> customer);
    }

    @Override
    public CompletableFuture<Optional<Customer>> findById(String customerId) {
        return customerTable.getItem(r -> r.key(k -> k.partitionValue(customerId)))
                .thenApply(Optional::ofNullable);
    }

    @Override
    public CompletableFuture<Optional<Customer>> findByEmail(String email) {
        // Note: For production, looking up by a non-key attribute requires creating a
        // Global Secondary Index (GSI) on the 'email' field for efficiency.
        // A full table scan with a filter is inefficient and costly at scale.
        // We use it here to make the service logic work for this project.

        Expression filterExpression = Expression.builder()
                .expression("email = :emailVal")
                .expressionValues(Map.of(":emailVal", AttributeValue.builder().s(email).build()))
                .build();

        CompletableFuture<Optional<Customer>> future = new CompletableFuture<>();

        customerTable.scan(req -> req.filterExpression(filterExpression))
                .items()
                .limit(1) // We only need to know if at least one item exists.
                .subscribe(new Subscriber<>() {
                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.subscription = s;
                        // Request the first item.
                        s.request(1);
                    }

                    @Override
                    public void onNext(Customer customer) {
                        // Item found, complete the future and cancel the subscription.
                        future.complete(Optional.of(customer));
                        subscription.cancel();
                    }

                    @Override
                    public void onError(Throwable t) {
                        // Error occurred during the stream, complete the future exceptionally.
                        future.completeExceptionally(t);
                    }

                    @Override
                    public void onComplete() {
                        // The stream completed without emitting any items.
                        // If the future is not already done, complete it with an empty Optional.
                        if (!future.isDone()) {
                            future.complete(Optional.empty());
                        }
                    }
                });

        return future;
    }
}
