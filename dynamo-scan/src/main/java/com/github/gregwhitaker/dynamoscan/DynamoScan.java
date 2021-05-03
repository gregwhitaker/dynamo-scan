package com.github.gregwhitaker.dynamoscan;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scans a dynamodb table.
 */
public class DynamoScan {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoScan.class);

    private final DynamoDB dynamoClient;
    private final String tableName;
    private final int totalSegments;
    private final ExecutorService executorService;

    /**
     * Creates a new instance of {@link DynamoScan}.
     *
     * @param amazonDynamoDB dynamodb client
     * @param tableName name of table to scan
     */
    public DynamoScan(final AmazonDynamoDB amazonDynamoDB,
                      final String tableName) {
        this.dynamoClient = new DynamoDB(amazonDynamoDB);
        this.tableName = tableName;
        this.totalSegments = Runtime.getRuntime().availableProcessors() * 2;
        this.executorService = Executors.newFixedThreadPool(totalSegments);
    }

    /**
     * Executes the table scan.
     *
     * @return a Flux of {@link Item}
     */
    public Flux<Item> scan() {
        return Flux.create(fluxSink -> {
            final List<ScanTask> tasksToExecute = new ArrayList<>();
            for (int segment = 0; segment < totalSegments; segment++) {
                tasksToExecute.add(new ScanTask(dynamoClient, segment, totalSegments, tableName, fluxSink));
            }

            try {
                executorService.invokeAll(tasksToExecute);
                fluxSink.complete();
            } catch (InterruptedException e) {
                Mono.error(e);
            } finally {
                executorService.shutdownNow();
            }
        });
    }

    /**
     * Task that scans a segment of the dynamo table.
     */
    static class ScanTask implements Callable<Void> {
        private final DynamoDB dynamoClient;
        private final int segment;
        private final int totalSegments;
        private final String tableName;
        private final FluxSink<Item> fluxSink;

        /**
         * Creates a new instance of {@link ScanTask}.
         *
         * @param dynamoClient dynamodb client
         * @param segment segment to scan
         * @param totalSegments total number of segments
         * @param tableName name of table to scan
         * @param fluxSink flux sink that emits scanned rows
         */
        public ScanTask(final DynamoDB dynamoClient,
                        final int segment,
                        final int totalSegments,
                        final String tableName,
                        final FluxSink<Item> fluxSink) {
            this.dynamoClient = dynamoClient;
            this.segment = segment;
            this.totalSegments = totalSegments;
            this.tableName = tableName;
            this.fluxSink = fluxSink;
        }

        @Override
        public Void call() throws Exception {
            final Table table = dynamoClient.getTable(tableName);

            Map<String, AttributeValue> lastEvaluatedKey = null;
            do {
                final ScanSpec spec = new ScanSpec()
                    .withConsistentRead(false)
                    .withTotalSegments(totalSegments)
                    .withSegment(segment);

                final ItemCollection<ScanOutcome> items = table.scan(spec);
                IteratorSupport<Item, ScanOutcome> iterator = items.iterator();

                while (iterator.hasNext()) {
                    fluxSink.next(iterator.next());
                }

                lastEvaluatedKey = items.getLastLowLevelResult().getScanResult().getLastEvaluatedKey();
            } while (lastEvaluatedKey != null);

            return null;
        }
    }
}
