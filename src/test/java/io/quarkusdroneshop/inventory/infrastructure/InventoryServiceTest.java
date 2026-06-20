package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkusdroneshop.inventory.domain.Inventory;
import io.quarkusdroneshop.inventory.domain.Item;
import io.quarkusdroneshop.inventory.domain.ProductMaster;
import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest @QuarkusTestResource(KafkaTestResource.class)
public class InventoryServiceTest {

    @Inject
    InventoryService inventoryService;

    @Inject
    @Any
    InMemoryConnector connector;

    InMemorySink<Integer> results;

    @BeforeEach @Transactional
    public void setUp() {

        results = connector.sink("inventory-out");
        ProductMaster productMaster = new ProductMaster(UUID.randomUUID(), Item.QDC_A101);
        Inventory inventory = new Inventory(productMaster,
                1.99,
                3.49,
                1,
                99,
                0,
                LocalDate.of(2025, 5, 10),
                LocalDate.now(),
                99,
                1000,
                21);
        productMaster.persist();
        inventory.persistAndFlush();
    }

    @Test
    public void testRestockItem() {

        RestockItemCommand restockItemCommand = new RestockItemCommand(Item.QDC_A101);
        inventoryService.restockItem(restockItemCommand);
        await().atMost(5, TimeUnit.SECONDS)
               .until(() -> results.received().size() >= 1);
        assertEquals(1, results.received().size());
    }

}
