package io.quarkusdroneshop.inventory.infrastructure;

import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
import io.quarkusdroneshop.inventory.domain.Item;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * drone-component-stock データプロダクトが公開する dataproduct-component-stock-quantity
 * (upsert-kafka, Avro) を ComponentStockQuantityUpdate に変換する。QDCA10 の
 * ComponentStockQuantityDeserializer と同じ方式 (Apicurio の Avro デシリアライザに委譲)。
 */
public class ComponentStockQuantityDeserializer implements Deserializer<ComponentStockQuantityUpdate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentStockQuantityDeserializer.class);

    private final AvroKafkaDeserializer<GenericRecord> avroDeserializer = new AvroKafkaDeserializer<>();

    @Override
    public void configure(java.util.Map<String, ?> configs, boolean isKey) {
        avroDeserializer.configure(configs, isKey);
    }

    @Override
    public ComponentStockQuantityUpdate deserialize(String topic, byte[] data) {
        if (data == null) {
            // upsert-kafka のトゥームストーン (削除) は無視する。
            return null;
        }

        GenericRecord record = avroDeserializer.deserialize(topic, new RecordHeaders(), data);
        if (record == null) {
            return null;
        }

        try {
            Item item = Item.valueOf(record.get("item").toString());
            long quantity = (Long) record.get("quantity");
            return new ComponentStockQuantityUpdate(item, quantity);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Unknown item in component-stock-quantity record, skipping: {}", record);
            return null;
        } catch (Exception e) {
            LOGGER.warn("Failed to convert component-stock-quantity record: {}", record, e);
            return null;
        }
    }

    @Override
    public void close() {
        avroDeserializer.close();
    }
}
