package example;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.github.gregwhitaker.dynamoscan.DynamoScan;
import reactor.util.function.Tuples;

import java.util.concurrent.CountDownLatch;

/**
 * Example application that scans a dynamodb table.
 */
public class Application {

    /**
     * Starts the example application.
     *
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        final AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-1"))
            .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
            .build();

        // Create Scanner
        final DynamoScan dynamoScan = new DynamoScan(dynamoClient, "catalog.products");

        final CountDownLatch latch = new CountDownLatch(1);

        // Run Scan
        dynamoScan.scan()
            .map(item -> Tuples.of(item.getString("id"), item.getString("name")))
            .doOnComplete(latch::countDown)
            .subscribe(objects -> System.out.printf("Product: %s - %s%n", objects.getT1(), objects.getT2()));

        latch.await();
    }
}
