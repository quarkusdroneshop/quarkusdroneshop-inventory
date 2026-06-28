# quarkusdroneshop-inventory

Quarkus ベースの在庫管理マイクロサービス。ドリンク製造マイクロサービス (QDCA10/QDCA10Pro) からの在庫切れ (86'd) イベントを受け取り、在庫補充コマンドを処理します。

- **バージョン**: 5.0.0-SNAPSHOT
- **Quarkus**: 3.33.2

## アーキテクチャ

```
quarkusdroneshop-qdca10 / quarkusdroneshop-qdca10pro
    │  inventory-in (dev) / shop-asite.inventory-in (prod) ──▶
    ▼
quarkusdroneshop-inventory
    │
    └──▶ inventory-out    (補充完了通知)
```

## Kafka トピック

| チャネル | dev トピック | prod トピック | 方向 |
|---|---|---|---|
| inventory-in | `inventory-in` | `shop-asite.inventory-in` | 受信 |
| inventory-out | `inventory-out` | `inventory-out` | 送信 |

## 在庫補充コマンド例

```json
{"commandType":"RESTOCK_INVENTORY_COMMAND","item":"QDC_A101","quantity":0}
```

## ローカル開発

```shell
git clone https://github.com/quarkusdroneshop/quarkusdroneshop-support.git
cd quarkusdroneshop-support
docker compose up

cd ../quarkusdroneshop-inventory
./mvnw quarkus:dev
```

## 環境変数 (本番)

| 変数名 | 説明 |
|---|---|
| `KAFKA_BOOTSTRAP_URLS` | Kafka ブローカー URL |

## パッケージング

```shell
# JVM モード
./mvnw package
java -jar target/quarkus-coffesshop-inventory-1.0.0-SNAPSHOT-runner.jar

# ネイティブビルド
./mvnw package -Pnative -Dquarkus.native.container-build=true
./target/quarkus-coffesshop-inventory-1.0.0-SNAPSHOT-runner
```

## 参考

- [Quarkus](https://quarkus.io/)
- [quarkusdroneshop.github.io](https://quarkusdroneshop.github.io)
