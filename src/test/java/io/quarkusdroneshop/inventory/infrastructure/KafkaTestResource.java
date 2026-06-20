package io.quarkusdroneshop.inventory.infrastructure;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

import java.util.Collections;
import java.util.Map;

/**
 * チャネルの切り替えは application.properties の %test プロファイルで静的に行う。
 * このクラスはテスト間の状態クリアのみを担う。
 */
public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}
