package io.quarkusdroneshop.inventory.domain.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusdroneshop.inventory.domain.Inventory;

import java.time.Instant;
import java.util.UUID;

public class RestockCompletedEvent implements RestockEvent, ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private static final String TYPE = "RESTOCK";
    private static final String EVENT_TYPE = "RESTOCK_COMPLETED_EVENT";

    private final String aggregateId;

    private final JsonNode jsonNode;

    private final Instant timestamp;

    public RestockCompletedEvent(String aggregateId, JsonNode jsonNode, Instant instant) {
        this.aggregateId = aggregateId;
        this.jsonNode = jsonNode;
        this.timestamp = instant;
    }

    public static RestockCompletedEvent from(final Inventory inventory) {


        // drone-component-stock の Flink ジョブ (component-stock-job.sql) は
        // eventType をペイロード内のトップレベルフィールドとして期待するため、
        // outboxevent テーブルの type 列と重複するが payload 内にも含めておく
        // (Debezium Outbox の標準構成では type 列は payload に自動転記されないため)。
        ObjectNode asJson = mapper.createObjectNode()
                .put("skuId", inventory.getProductMaster().getSkuId().toString())
                .put("item", inventory.getProductMaster().getItem().toString())
                .put("eventType", EVENT_TYPE)
                .put("quantity", inventory.getInStockQuantity());

        return new RestockCompletedEvent(
                inventory.getProductMaster().getSkuId().toString(), // UUID → String に変換
                asJson,
                Instant.now());
    }

    @Override
    public String getAggregateId() {
        return this.aggregateId;
    }

    @Override
    public String getAggregateType() {
        return TYPE;
    }

    @Override
    public String getType() {
        return EVENT_TYPE;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return jsonNode;
    }
}
