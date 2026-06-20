package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkusdroneshop.inventory.domain.Item;
import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest @QuarkusTestResource(KafkaTestResource.class)
public class TestKafkaService {

    static final Logger LOGGER = LoggerFactory.getLogger(TestKafkaService.class);

    String KAKFA_TOPIC = "inventory-in";

    @Inject
    @Any
    InMemoryConnector connector;

    @InjectSpy
    InventoryService inventoryService;

    @BeforeEach
    public void setUp() {
        doAnswer(invocationOnMock -> new CompletableFuture<RestockItemCommand>()).when(inventoryService).restockItem(any(RestockItemCommand.class));
    }

    @Test
    public void testIncoming() {

        RestockItemCommand restockInventoryCommand = new RestockItemCommand(Item.QDC_A101);
        InMemorySource<RestockItemCommand> ordersIn = connector.source(KAKFA_TOPIC);
        ordersIn.send(restockInventoryCommand);
        LOGGER.info("sent to Kafka: {}", restockInventoryCommand);
        await().atMost(5, TimeUnit.SECONDS)
               .untilAsserted(() -> verify(inventoryService, times(1)).restockItem(new RestockItemCommand(Item.QDC_A101)));
        LOGGER.info("verifying");
    }
}
