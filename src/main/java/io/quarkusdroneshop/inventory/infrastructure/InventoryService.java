package io.quarkusdroneshop.inventory.infrastructure;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusdroneshop.inventory.domain.Inventory;
import io.quarkusdroneshop.inventory.domain.RestockInventoryCommand;
import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import io.quarkusdroneshop.inventory.domain.RestockItemResult;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Inject
    @Channel("inventory-out")
    Emitter<String> inventoryEmitter;

    public void restockItem(final RestockItemCommand restockItemCommand) {
        LOGGER.debug("restockItem: {}", restockItemCommand);

        Inventory inventory = inventoryRepository.findByItem(restockItemCommand.getItem());//Inventory.find("#Inventory.findByItem", restockItemCommand.getItem()).firstResult();
        LOGGER.debug("inventory: {}", inventory);

        RestockItemResult restockItemResult = inventory.restock();
        LOGGER.debug("RestockItemResult: {}", restockItemResult);

        restockItemResult.getRestockEvents().forEach(exportedEvent -> {
            event.fire(exportedEvent);
            LOGGER.debug("fired: {}", exportedEvent);
        });

        restockItemResult.getRestockInventoryCommands().forEach(command -> {
            LOGGER.debug("sending: {}", command);
            inventoryEmitter.send(command.toString());
            LOGGER.debug("sent: {}", command);
        });

        LOGGER.debug("restock completed");
    }

}
