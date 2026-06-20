package io.quarkusdroneshop.inventory.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class RestockInventoryCommand implements DroneshopCommand{

    Item item;

    int quantity;

    public RestockInventoryCommand() {
        super();
    }

    public RestockInventoryCommand(Item item) {
        this.item = item;
        this.quantity = 0;
    }

    public RestockInventoryCommand(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "RestockInventoryCommand{" +
                "item=" + item +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestockInventoryCommand that = (RestockInventoryCommand) o;
        if (quantity != that.quantity) return false;
        return item == that.item;
    }

    @Override
    public int hashCode() {
        int result = (item != null ? item.hashCode() : 0);
        result = 31 * result + quantity;
        return result;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
