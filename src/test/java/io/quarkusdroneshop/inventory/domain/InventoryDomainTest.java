package io.quarkusdroneshop.inventory.domain;

import io.quarkusdroneshop.inventory.domain.events.RestockCompletedEvent;
import io.quarkusdroneshop.inventory.domain.events.RestockRequestedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ドメインオブジェクトの純粋ユニットテスト（Quarkusコンテナ不要）
 */
public class InventoryDomainTest {

    // ── ヘルパー ──────────────────────────────────────────────────────────────

    private Inventory buildInventory(Item item, int inStock, int reserved) {
        ProductMaster pm = new ProductMaster(UUID.randomUUID(), item);
        return new Inventory(pm, 1.99, 3.99, 10, inStock, 0,
                LocalDate.of(2024, 1, 1), LocalDate.now(), 5, 200, reserved);
    }

    // ── Inventory エンティティ ────────────────────────────────────────────────

    @Test
    void testAvailableQuantity() {
        Inventory inv = buildInventory(Item.QDC_A101, 50, 10);
        assertEquals(40, inv.availableQuantity());
    }

    @Test
    void testAvailableQuantity_zero() {
        Inventory inv = buildInventory(Item.QDC_A101, 0, 0);
        assertEquals(0, inv.availableQuantity());
    }

    @Test
    void testRestock_QDC_A101() {
        Inventory inv = buildInventory(Item.QDC_A101, 50, 0);
        RestockItemResult result = inv.restock();
        assertNotNull(result);
        assertEquals(1, result.getRestockInventoryCommands().size());
        assertEquals(2, result.getRestockEvents().size());
        assertEquals(Item.QDC_A101, result.getRestockInventoryCommands().get(0).getItem());
        assertEquals(99, result.getRestockInventoryCommands().get(0).getQuantity());
    }

    @Test
    void testRestock_QDC_A102() {
        RestockItemResult result = buildInventory(Item.QDC_A102, 50, 0).restock();
        assertNotNull(result);
        assertEquals(1, result.getRestockInventoryCommands().size());
    }

    @Test
    void testRestock_QDC_A103() {
        RestockItemResult result = buildInventory(Item.QDC_A103, 50, 0).restock();
        assertNotNull(result);
    }

    @Test
    void testRestock_QDC_A104_AC() {
        RestockItemResult result = buildInventory(Item.QDC_A104_AC, 50, 0).restock();
        assertNotNull(result);
    }

    @Test
    void testRestock_QDC_A104_AT() {
        RestockItemResult result = buildInventory(Item.QDC_A104_AT, 50, 0).restock();
        assertNotNull(result);
    }

    @Test
    void testRestock_default_branch() {
        // QDC_A105_Pro01 は switch の default に該当
        RestockItemResult result = buildInventory(Item.QDC_A105_Pro01, 50, 0).restock();
        assertNotNull(result);
    }

    @Test
    void testInventory_gettersAndSetters() {
        ProductMaster pm = new ProductMaster(UUID.randomUUID(), Item.QDC_A101);
        Inventory inv = new Inventory();
        inv.setProductMaster(pm);
        inv.setUnitCost(2.00);
        inv.setMaxRetailPrice(4.00);
        inv.setOrderQuantity(5);
        inv.setInStockQuantity(100);
        inv.setBackOrderQuantity(10);
        LocalDate stock = LocalDate.of(2024, 1, 1);
        LocalDate sale = LocalDate.of(2024, 6, 1);
        inv.setLastStockDate(stock);
        inv.setLastSaleDate(sale);
        inv.setMinimumQuantity(10);
        inv.setMaximumQuantity(200);
        inv.setReservedQuantity(5);

        assertSame(pm, inv.getProductMaster());
        assertEquals(2.00, inv.getUnitCost());
        assertEquals(4.00, inv.getMaxRetailPrice());
        assertEquals(5, inv.getOrderQuantity());
        assertEquals(100, inv.getInStockQuantity());
        assertEquals(10, inv.getBackOrderQuantity());
        assertEquals(stock, inv.getLastStockDate());
        assertEquals(sale, inv.getLastSaleDate());
        assertEquals(10, inv.getMinimumQuantity());
        assertEquals(200, inv.getMaximumQuantity());
        assertEquals(5, inv.getReservedQuantity());
    }

    @Test
    void testInventory_equalsAndHashCode() {
        ProductMaster pm = new ProductMaster(UUID.randomUUID(), Item.QDC_A101);
        LocalDate date = LocalDate.of(2024, 1, 1);
        Inventory a = new Inventory(pm, 1.99, 3.99, 10, 50, 0, date, date, 5, 200, 0);
        Inventory b = new Inventory(pm, 1.99, 3.99, 10, 50, 0, date, date, 5, 200, 0);
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
        assertNotEquals(a, "other");
    }

    @Test
    void testInventory_toString() {
        Inventory inv = buildInventory(Item.QDC_A101, 50, 0);
        String s = inv.toString();
        assertTrue(s.contains("Inventory{"));
    }

    // ── ProductMaster ─────────────────────────────────────────────────────────

    @Test
    void testProductMaster_constructorAndGetters() {
        UUID uuid = UUID.randomUUID();
        ProductMaster pm = new ProductMaster(uuid, Item.QDC_A102);
        assertEquals(uuid, pm.getSkuId());
        assertEquals(Item.QDC_A102, pm.getItem());
    }

    @Test
    void testProductMaster_setters() {
        ProductMaster pm = new ProductMaster();
        UUID uuid = UUID.randomUUID();
        pm.setSkuId(uuid);
        pm.setItem(Item.QDC_A103);
        assertEquals(uuid, pm.getSkuId());
        assertEquals(Item.QDC_A103, pm.getItem());
    }

    // ── RestockItemCommand ────────────────────────────────────────────────────

    @Test
    void testRestockItemCommand_defaultConstructor() {
        RestockItemCommand cmd = new RestockItemCommand();
        assertNull(cmd.getItem());
        assertEquals(0, cmd.getQuantity());
    }

    @Test
    void testRestockItemCommand_itemOnly() {
        RestockItemCommand cmd = new RestockItemCommand(Item.QDC_A101);
        assertEquals(Item.QDC_A101, cmd.getItem());
        assertEquals(0, cmd.getQuantity());
    }

    @Test
    void testRestockItemCommand_itemAndQuantity() {
        RestockItemCommand cmd = new RestockItemCommand(Item.QDC_A102, 50);
        assertEquals(Item.QDC_A102, cmd.getItem());
        assertEquals(50, cmd.getQuantity());
    }

    @Test
    void testRestockItemCommand_setters() {
        RestockItemCommand cmd = new RestockItemCommand();
        cmd.setItem(Item.QDC_A103);
        cmd.setQuantity(30);
        assertEquals(Item.QDC_A103, cmd.getItem());
        assertEquals(30, cmd.getQuantity());
    }

    @Test
    void testRestockItemCommand_equalsAndHashCode() {
        RestockItemCommand a = new RestockItemCommand(Item.QDC_A101, 10);
        RestockItemCommand b = new RestockItemCommand(Item.QDC_A101, 10);
        RestockItemCommand c = new RestockItemCommand(Item.QDC_A102, 10);
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "other");
    }

    @Test
    void testRestockItemCommand_toString() {
        RestockItemCommand cmd = new RestockItemCommand(Item.QDC_A101, 5);
        assertTrue(cmd.toString().contains("QDC_A101"));
    }

    // ── RestockInventoryCommand ───────────────────────────────────────────────

    @Test
    void testRestockInventoryCommand_defaultConstructor() {
        RestockInventoryCommand cmd = new RestockInventoryCommand();
        assertNull(cmd.getItem());
        assertEquals(0, cmd.getQuantity());
    }

    @Test
    void testRestockInventoryCommand_itemOnly() {
        RestockInventoryCommand cmd = new RestockInventoryCommand(Item.QDC_A101);
        assertEquals(Item.QDC_A101, cmd.getItem());
        assertEquals(0, cmd.getQuantity());
    }

    @Test
    void testRestockInventoryCommand_itemAndQuantity() {
        RestockInventoryCommand cmd = new RestockInventoryCommand(Item.QDC_A102, 99);
        assertEquals(Item.QDC_A102, cmd.getItem());
        assertEquals(99, cmd.getQuantity());
    }

    @Test
    void testRestockInventoryCommand_setters() {
        RestockInventoryCommand cmd = new RestockInventoryCommand();
        cmd.setItem(Item.QDC_A103);
        cmd.setQuantity(20);
        assertEquals(Item.QDC_A103, cmd.getItem());
        assertEquals(20, cmd.getQuantity());
    }

    @Test
    void testRestockInventoryCommand_equalsAndHashCode() {
        RestockInventoryCommand a = new RestockInventoryCommand(Item.QDC_A101, 10);
        RestockInventoryCommand b = new RestockInventoryCommand(Item.QDC_A101, 10);
        RestockInventoryCommand c = new RestockInventoryCommand(Item.QDC_A102, 10);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "other");
    }

    @Test
    void testRestockInventoryCommand_toString() {
        RestockInventoryCommand cmd = new RestockInventoryCommand(Item.QDC_A104_AT, 5);
        assertTrue(cmd.toString().contains("QDC_A104_AT"));
    }

    // ── RestockItemResult ─────────────────────────────────────────────────────

    @Test
    void testRestockItemResult_gettersAndSetters() {
        Inventory inv = buildInventory(Item.QDC_A101, 50, 0);
        RestockItemResult result = inv.restock();

        List<RestockInventoryCommand> cmds = result.getRestockInventoryCommands();
        assertNotNull(cmds);
        assertNotNull(result.getRestockEvents());
        assertTrue(result.toString().contains("RestockItemResult{"));

        RestockItemResult a = inv.restock();
        RestockItemResult b = inv.restock();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null);
    }

    @Test
    void testRestockItemResult_setters() {
        Inventory inv = buildInventory(Item.QDC_A101, 50, 0);
        RestockItemResult result = inv.restock();
        List<RestockInventoryCommand> cmds = result.getRestockInventoryCommands();
        result.setRestockInventoryCommands(cmds);
        result.setRestockEvents(result.getRestockEvents());
        assertEquals(cmds, result.getRestockInventoryCommands());
    }

    // ── RestockRequestedEvent ─────────────────────────────────────────────────

    @Test
    void testRestockRequestedEvent_from() {
        Inventory inv = buildInventory(Item.QDC_A101, 50, 0);
        RestockRequestedEvent event = RestockRequestedEvent.from(inv);
        assertNotNull(event);
        assertEquals("RESTOCK", event.getAggregateType());
        assertEquals("RESTOCK_REQUESTED_EVENT", event.getType());
        assertNotNull(event.getAggregateId());
        assertNotNull(event.getTimestamp());
        assertNotNull(event.getPayload());
    }

    @Test
    void testRestockRequestedEvent_constructor() {
        String aggId = UUID.randomUUID().toString();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode().put("key", "value");
        Instant now = Instant.now();
        RestockRequestedEvent event = new RestockRequestedEvent(aggId, node, now);
        assertEquals(aggId, event.getAggregateId());
        assertEquals(now, event.getTimestamp());
        assertEquals(node, event.getPayload());
    }

    // ── RestockCompletedEvent ─────────────────────────────────────────────────

    @Test
    void testRestockCompletedEvent_from() {
        Inventory inv = buildInventory(Item.QDC_A102, 50, 0);
        RestockCompletedEvent event = RestockCompletedEvent.from(inv);
        assertNotNull(event);
        assertEquals("RESTOCK", event.getAggregateType());
        assertEquals("RESTOCK_COMPLETED_EVENT", event.getType());
        assertNotNull(event.getAggregateId());
        assertNotNull(event.getTimestamp());
        assertNotNull(event.getPayload());
    }

    @Test
    void testRestockCompletedEvent_constructor() {
        String aggId = UUID.randomUUID().toString();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode().put("item", "QDC_A101");
        Instant now = Instant.now();
        RestockCompletedEvent event = new RestockCompletedEvent(aggId, node, now);
        assertEquals(aggId, event.getAggregateId());
        assertEquals(now, event.getTimestamp());
    }

    // ── Item enum ─────────────────────────────────────────────────────────────

    @Test
    void testItem_allValues() {
        Item[] values = Item.values();
        assertEquals(9, values.length);
        assertEquals(Item.QDC_A101, Item.valueOf("QDC_A101"));
        assertEquals(Item.QDC_A102, Item.valueOf("QDC_A102"));
        assertEquals(Item.QDC_A103, Item.valueOf("QDC_A103"));
        assertEquals(Item.QDC_A104_AC, Item.valueOf("QDC_A104_AC"));
        assertEquals(Item.QDC_A104_AT, Item.valueOf("QDC_A104_AT"));
        assertEquals(Item.QDC_A105_Pro01, Item.valueOf("QDC_A105_Pro01"));
        assertEquals(Item.QDC_A105_Pro02, Item.valueOf("QDC_A105_Pro02"));
        assertEquals(Item.QDC_A105_Pro03, Item.valueOf("QDC_A105_Pro03"));
        assertEquals(Item.QDC_A105_Pro04, Item.valueOf("QDC_A105_Pro04"));
    }

    // ── Originator enum ───────────────────────────────────────────────────────

    @Test
    void testOriginator_allValues() {
        assertEquals(3, Originator.values().length);
        assertEquals(Originator.QDCA10, Originator.valueOf("QDCA10"));
        assertEquals(Originator.INVENTORY, Originator.valueOf("INVENTORY"));
        assertEquals(Originator.QDCA10PRO, Originator.valueOf("QDCA10PRO"));
    }
}
