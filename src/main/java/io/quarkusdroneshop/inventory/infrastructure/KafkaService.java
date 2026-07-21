package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import io.smallrye.common.annotation.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class KafkaService {

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Inject
    InventoryService inventoryService;

    @Incoming("inventory-in")
    @Blocking
    @Transactional
    public void processRestockCommand(final RestockItemCommand restockItemCommand) {
        logger.debug("\nRestockItemCommand Received: {}", restockItemCommand);
        inventoryService.restockItem(restockItemCommand);
    }

    // drone-component-stock データプロダクト (dataproduct-component-stock-quantity) を
    // 購読し、QDCA10/QDCA10pro 側の実消費 (component-stock-decrement 由来の upsert も含む)
    // を在庫サービスのローカル Postgres へ反映する。restock() ドメインイベント経路とは
    // 独立した読み取り専用の同期であり、Outbox イベントは発行しない (無限ループ防止)。
    @Incoming("component-stock-quantity")
    @Blocking
    @Transactional
    public void onComponentStockQuantity(final ComponentStockQuantityUpdate update) {
        if (update == null) {
            return;
        }
        logger.debug("ComponentStockQuantityUpdate received: item={}, quantity={}", update.getItem(), update.getQuantity());
        inventoryService.syncQuantity(update.getItem(), (int) update.getQuantity());
    }

}
