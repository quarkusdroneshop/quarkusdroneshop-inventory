package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkusdroneshop.inventory.domain.Item;

public class ComponentStockQuantityUpdate {

    private final Item item;

    private final long quantity;

    public ComponentStockQuantityUpdate(Item item, long quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public long getQuantity() {
        return quantity;
    }
}
