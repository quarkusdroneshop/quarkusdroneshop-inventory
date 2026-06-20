package io.quarkusdroneshop.inventory.domain;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkusdroneshop.inventory.domain.events.RestockCompletedEvent;
import io.quarkusdroneshop.inventory.domain.events.RestockEvent;
import io.quarkusdroneshop.inventory.domain.events.RestockRequestedEvent;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @NamedQuery(name="Inventory.findByItem", query="from Inventory where productMaster.item = ?1")
public class Inventory extends PanacheEntity {

    @OneToOne
    ProductMaster productMaster;

    Double unitCost;

    Double maxRetailPrice;

    int orderQuantity;

    int inStockQuantity;

    int backOrderQuantity;

    LocalDate lastStockDate;

    LocalDate lastSaleDate;

    int minimumQuantity;

    int maximumQuantity;

    int reservedQuantity;

    public int availableQuantity(){
        return inStockQuantity - reservedQuantity;
    }

    public RestockItemResult restock() {

        RestockInventoryCommand restockInventoryCommand = new RestockInventoryCommand(this.productMaster.item, 99);

        List<ExportedEvent> restockEventList = new ArrayList<ExportedEvent>();
        restockEventList.add(RestockRequestedEvent.from(this));

        // model the restocking time making the drink
        int delay;
        switch (this.productMaster.item) {
            case QDC_A101:
                delay = 50;
                break;
            case QDC_A102:
                delay = 50;
                break;
            case QDC_A103:
                delay = 70;
                break;
            case QDC_A104_AC:
                delay = 70;
                break;
            case QDC_A104_AT:
                delay = 100;
                break;
            default:
                delay = 300;
                break;
        };

        restockEventList.add(RestockCompletedEvent.from(this));

        return new RestockItemResult(
                new ArrayList<RestockInventoryCommand>(){{
                    add(restockInventoryCommand);
                }},
                restockEventList);
    }

    public Inventory() {
    }

    public Inventory(ProductMaster productMaster, Double unitCost, Double maxRetailPrice, int orderQuantity, int inStockQuantity, int backOrderQuantity, LocalDate lastStockDate, LocalDate lastSaleDate, int minimumQuantity, int maximumQuantity, int reservedQuantity) {
        this.productMaster = productMaster;
        this.unitCost = unitCost;
        this.maxRetailPrice = maxRetailPrice;
        this.orderQuantity = orderQuantity;
        this.inStockQuantity = inStockQuantity;
        this.backOrderQuantity = backOrderQuantity;
        this.lastStockDate = lastStockDate;
        this.lastSaleDate = lastSaleDate;
        this.minimumQuantity = minimumQuantity;
        this.maximumQuantity = maximumQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "productMaster=" + productMaster +
                ", unitCost=" + unitCost +
                ", maxRetailPrice=" + maxRetailPrice +
                ", orderQuantity=" + orderQuantity +
                ", inStockQuantity=" + inStockQuantity +
                ", backOrderQuantity=" + backOrderQuantity +
                ", lastStockDate=" + lastStockDate +
                ", lastSaleDate=" + lastSaleDate +
                ", minimumQuantity=" + minimumQuantity +
                ", maximumQuantity=" + maximumQuantity +
                ", reservedQuantity=" + reservedQuantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inventory inventory = (Inventory) o;

        if (orderQuantity != inventory.orderQuantity) return false;
        if (inStockQuantity != inventory.inStockQuantity) return false;
        if (backOrderQuantity != inventory.backOrderQuantity) return false;
        if (minimumQuantity != inventory.minimumQuantity) return false;
        if (maximumQuantity != inventory.maximumQuantity) return false;
        if (reservedQuantity != inventory.reservedQuantity) return false;
        if (productMaster != null ? !productMaster.equals(inventory.productMaster) : inventory.productMaster != null)
            return false;
        if (unitCost != null ? !unitCost.equals(inventory.unitCost) : inventory.unitCost != null) return false;
        if (maxRetailPrice != null ? !maxRetailPrice.equals(inventory.maxRetailPrice) : inventory.maxRetailPrice != null)
            return false;
        if (lastStockDate != null ? !lastStockDate.equals(inventory.lastStockDate) : inventory.lastStockDate != null)
            return false;
        return lastSaleDate != null ? lastSaleDate.equals(inventory.lastSaleDate) : inventory.lastSaleDate == null;
    }

    @Override
    public int hashCode() {
        int result = productMaster != null ? productMaster.hashCode() : 0;
        result = 31 * result + (unitCost != null ? unitCost.hashCode() : 0);
        result = 31 * result + (maxRetailPrice != null ? maxRetailPrice.hashCode() : 0);
        result = 31 * result + orderQuantity;
        result = 31 * result + inStockQuantity;
        result = 31 * result + backOrderQuantity;
        result = 31 * result + (lastStockDate != null ? lastStockDate.hashCode() : 0);
        result = 31 * result + (lastSaleDate != null ? lastSaleDate.hashCode() : 0);
        result = 31 * result + minimumQuantity;
        result = 31 * result + maximumQuantity;
        result = 31 * result + reservedQuantity;
        return result;
    }

    public ProductMaster getProductMaster() {
        return productMaster;
    }

    public void setProductMaster(ProductMaster productMaster) {
        this.productMaster = productMaster;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    public Double getMaxRetailPrice() {
        return maxRetailPrice;
    }

    public void setMaxRetailPrice(Double maxRetailPrice) {
        this.maxRetailPrice = maxRetailPrice;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public int getInStockQuantity() {
        return inStockQuantity;
    }

    public void setInStockQuantity(int inStockQuantity) {
        this.inStockQuantity = inStockQuantity;
    }

    public int getBackOrderQuantity() {
        return backOrderQuantity;
    }

    public void setBackOrderQuantity(int backOrderQuantity) {
        this.backOrderQuantity = backOrderQuantity;
    }

    public LocalDate getLastStockDate() {
        return lastStockDate;
    }

    public void setLastStockDate(LocalDate lastStockDate) {
        this.lastStockDate = lastStockDate;
    }

    public LocalDate getLastSaleDate() {
        return lastSaleDate;
    }

    public void setLastSaleDate(LocalDate lastSaleDate) {
        this.lastSaleDate = lastSaleDate;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(int maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }
}
