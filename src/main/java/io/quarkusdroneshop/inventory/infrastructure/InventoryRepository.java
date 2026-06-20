package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.runtime.StartupEvent;
import io.quarkusdroneshop.inventory.domain.Inventory;
import io.quarkusdroneshop.inventory.domain.Item;
import io.quarkusdroneshop.inventory.domain.ProductMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class InventoryRepository implements PanacheRepository<Inventory> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryRepository.class);

    public Inventory findByItem(final Item item) {
        LOGGER.debug("findByItem: {}", item);
        Inventory result = Inventory.find("#Inventory.findByItem", item).firstResult();
        LOGGER.debug("found: {}", result);
        return result;
    }

    @Transactional
    void startup(@Observes StartupEvent event) {
        LOGGER.debug("loading inventory");
        loadInventory();
        LOGGER.debug("inventory loaded");
    }

    private void loadInventory() {

        Stream.of(Item.values()).forEach(item -> {
            ProductMaster productMaster = new ProductMaster(UUID.randomUUID(), item);
            productMaster.persist();
            Inventory inventory = new Inventory(
                    productMaster,
                    1.99,
                    3.99,
                    1,
                    99,
                    99,
                    LocalDate.of(2021, 10, 21),
                    LocalDate.now(),
                    33,
                    99,
                    0);
            inventory.persistAndFlush();
            LOGGER.debug("persisted: {}", inventory);
        });
    }
}
