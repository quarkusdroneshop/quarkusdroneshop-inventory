package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import io.smallrye.common.annotation.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class KafkaService {

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Inject
    InventoryService inventoryService;

    @Incoming("inventory-in")
    @Blocking
    @Transactional
    public void processRestockCommand(final RestockItemCommand restockItemCommand) {
        logger.debug("\nRestockItemCommand Received: {}", restockItemCommand);
        inventoryService.restockItem(restockItemCommand);
    }


}
