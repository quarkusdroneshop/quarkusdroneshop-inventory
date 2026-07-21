package io.quarkusdroneshop.inventory.infrastructure;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusdroneshop.inventory.domain.Inventory;
import io.quarkusdroneshop.inventory.domain.RestockItemCommand;
import io.quarkusdroneshop.inventory.domain.RestockItemResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    Event<ExportedEvent<?, ?>> event;

    public void restockItem(final RestockItemCommand restockItemCommand) {
        LOGGER.debug("restockItem: {}", restockItemCommand);

        Inventory inventory = inventoryRepository.findByItem(restockItemCommand.getItem());//Inventory.find("#Inventory.findByItem", restockItemCommand.getItem()).firstResult();
        LOGGER.debug("inventory: {}", inventory);

        RestockItemResult restockItemResult = inventory.restock(restockItemCommand.getQuantity());
        LOGGER.debug("RestockItemResult: {}", restockItemResult);

        restockItemResult.getRestockEvents().forEach(exportedEvent -> {
            event.fire(exportedEvent);
            LOGGER.debug("fired: {}", exportedEvent);
        });

        // restockItemResult.getRestockInventoryCommands() は以前 inventory-out に
        // command.toString() (非JSON) として直接送信していたが、これは inventory-out を
        // 購読する JSON コンシューマ (drone-component-stock の Flink ジョブ等) を
        // デシリアライズ失敗でクラッシュさせる不正なデータだった上、この Kafka 経由の
        // RestockInventoryCommand を実際に消費する処理はどこにも存在しなかった
        // (Debezium Outbox 経由の RestockRequestedEvent/RestockCompletedEvent が
        // inventory-out の正規の内容)。そのため送信自体を削除した。

        LOGGER.debug("restock completed");
    }

}
