package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkusdroneshop.inventory.domain.Inventory;
import io.quarkusdroneshop.inventory.domain.Item;
import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * C クラスタ (csite) の在庫管理ページ (homeoffice-backend 経由) から呼び出される
 * 管理用 REST API。inventory-in トピック (Kafka) は asite のミラーしか
 * 購読しておらず csite からは到達できないため、この管理操作は Kafka を経由せず
 * InventoryService を直接呼び出す同期 REST 経路として提供する。
 */
@Path("/inventory")
public class InventoryResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryResource.class);

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    InventoryService inventoryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<InventoryLevel> list() {
        return Stream.of(Item.values())
                .map(inventoryRepository::findByItem)
                .filter(inv -> inv != null)
                .map(InventoryLevel::from)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/restock")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public InventoryLevel restock(final RestockRequest request) {
        LOGGER.info("Admin restock request received: {}", request);

        if (request == null || request.item == null) {
            throw new jakarta.ws.rs.BadRequestException("item is required");
        }
        if (request.quantity < 0) {
            throw new jakarta.ws.rs.BadRequestException("quantity must be >= 0");
        }

        Inventory inventory = inventoryRepository.findByItem(request.item);
        if (inventory == null) {
            throw new NotFoundException("Unknown item: " + request.item);
        }

        inventoryService.restockItem(new RestockItemCommand(request.item, request.quantity));

        return InventoryLevel.from(inventoryRepository.findByItem(request.item));
    }

    public static class RestockRequest {
        public Item item;
        public int quantity;

        @Override
        public String toString() {
            return "RestockRequest{item=" + item + ", quantity=" + quantity + "}";
        }
    }

    public static class InventoryLevel {
        public Item item;
        public int inStockQuantity;

        public static InventoryLevel from(Inventory inventory) {
            InventoryLevel level = new InventoryLevel();
            level.item = inventory.getProductMaster().getItem();
            level.inStockQuantity = inventory.getInStockQuantity();
            return level;
        }
    }
}
