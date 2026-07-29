package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkusdroneshop.inventory.domain.Inventory;
import io.quarkusdroneshop.inventory.domain.Item;
import io.quarkusdroneshop.inventory.domain.ProductMaster;
import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest @QuarkusTestResource(KafkaTestResource.class)
public class InventoryServiceTest {

    @Inject
    InventoryService inventoryService;

    @Inject
    InventoryRepository inventoryRepository;

    @BeforeEach @Transactional
    public void setUp() {

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

    // restockItem() は以前 inventory-out へ直接メッセージを送っていたが、不正な
    // データを送出しているだけで実際の消費者が存在しなかったため送信自体を削除
    // 済み(InventoryService 参照)。実際に検証すべきは在庫数量の更新のみ。
    @Test
    @Transactional
    public void testRestockItem() {

        RestockItemCommand restockItemCommand = new RestockItemCommand(Item.QDC_A101, 99);
        inventoryService.restockItem(restockItemCommand);

        Inventory updated = inventoryRepository.findByItem(Item.QDC_A101);
        assertEquals(99, updated.getInStockQuantity());
    }

}
