package io.quarkusdroneshop.inventory.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class RestockItemCommand implements DroneshopCommand {

    Item item;

    int quantity;

    /**
     * Default empty constructor
     */
    public RestockItemCommand() {
    }

    /**
     * Constructor for use when an item is 86'd
     *
     * @param item Item
     */
    public RestockItemCommand(Item item) {
        this.item = item;
        this.quantity = 0;
    }

    /**
     *
     * @param item Item
     * @param quantity int
     */
    public RestockItemCommand(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "RestockItemCommand{"
                + "item=" + item
                + ", quantity=" + quantity
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RestockItemCommand that = (RestockItemCommand) o;

        if (quantity != that.quantity) {
            return false;
        }
        return item == that.item;
    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
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
