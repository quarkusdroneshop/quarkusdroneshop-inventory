package io.quarkusdroneshop.inventory.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @NamedQuery(name="ProductMaster.findBySkuId", query="from ProductMaster where skuId = ?1")
public class ProductMaster extends PanacheEntityBase {

    @Id @Column(unique = true, name="sku_id")
    UUID skuId;

    @Enumerated(EnumType.STRING)
    Item item;

    public ProductMaster() {
    }

    public ProductMaster(UUID skuId, Item item) {
        this.skuId = skuId;
        this.item = item;
    }

    @Override
    public String toString() {
        return "ProductMaster{" +
                "skuId=" + skuId +
                ", description='" + item + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductMaster that = (ProductMaster) o;

        if (skuId != null ? !skuId.equals(that.skuId) : that.skuId != null) return false;
        return item != null ? item.equals(that.item) : that.item == null;
    }

    @Override
    public int hashCode() {
        int result = skuId != null ? skuId.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

    public UUID getSkuId() {
        return skuId;
    }

    public void setSkuId(UUID skuId) {
        this.skuId = skuId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item description) {
        this.item = description;
    }
}
