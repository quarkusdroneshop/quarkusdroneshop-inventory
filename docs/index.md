# Inventory マイクロサービス

## 概要

Inventory はドローンショップの **在庫管理マイクロサービス** です。

- Kafka から入庫・出庫イベントを受信
- PostgreSQL に在庫データを永続化
- 在庫不足アラートを送信

**フレームワーク**: Quarkus  
**デプロイ先クラスター**: b-cluster

---

## アーキテクチャ

```
Counter / QDCA10 / QDCA10Pro
        │
        ▼ Kafka: shop-asite-inventory-in
┌─────────────────┐
│    Inventory    │──► PostgreSQL (droneshopdb)
│                 │
│                 │──► Kafka: inventory-out（在庫更新通知）
└─────────────────┘
```

### Kafka トピック一覧

| トピック | 方向 | 説明 |
|---------|------|------|
| `shop-asite-inventory-in` | 受信 | a-cluster からの在庫操作指示 |
| `inventory-out` | 送信 | 在庫更新通知 |

### 依存サービス

- **PostgreSQL** (droneshopdb): 在庫データの永続化
- **Apache Kafka**: イベントメッセージング

---

## ローカル開発

### 前提条件

- Java 17+
- Docker / Docker Compose

### 1. インフラ起動

```shell
git clone https://github.com/quarkusdroneshop/quarkusdroneshop-support.git
cd quarkusdroneshop-support
docker compose up -d
```

### 2. アプリケーション起動

```shell
git clone https://github.com/quarkusdroneshop/quarkusdroneshop-inventory.git
cd quarkusdroneshop-inventory
./mvnw clean compile quarkus:dev
```

Dev UI: http://localhost:8080/q/dev

### 環境変数

| 変数名 | デフォルト | 説明 |
|--------|-----------|------|
| `KAFKA_BOOTSTRAP_URLS` | `localhost:9092` | Kafka ブートストラップアドレス |
| `PGSQL_URL` | `jdbc:postgresql://localhost:5432/droneshopdb?currentSchema=droneshop` | DB 接続 URL |
| `PGSQL_USER` | `droneshopuser` | DB ユーザー名 |
| `PGSQL_PASS` | `redhat-21` | DB パスワード |

---

## 本番デプロイ（Tekton Pipeline）

### パイプライン概要

```
fetch-repository → semgrep-scan → maven-run → push-oc-apps
```

| ステップ | 内容 |
|---------|------|
| `fetch-repository` | GitHub からソースをクローン |
| `semgrep-scan` | SAST セキュリティスキャン |
| `maven-run` | uber-jar ビルド |
| `push-oc-apps` | OpenShift b-cluster へデプロイ |

### 手動実行

```shell
tkn pipeline start build-and-push-quarkusdroneshop-inventory \
  -n quarkusdroneshop-cicd \
  --use-param-defaults
```

---

## テスト

```shell
# ユニットテスト(ArchUnit含む)
./mvnw test

# 統合テスト（Jacoco含む）
./mvnw verify

# チェックスタイル
./mvnw checkstyle:check

# PMD
./mvnw pmd:pmd

# SpotBugs
./mvnw spotbugs:spotbugs

# semgrep
semgrep scan --config p/default --json > target/semgrep-results.json

# secret scan
gitleaks detect --source . --report-format json --report-path target/gitleaks-report.json --exit-code 1

# 脆弱性テスト
trivy fs --scanners vuln,secret,misconfig,license --exit-code=1 --ignorefile ./.trivyignore.yaml ./ > target/trivy.txt

# セキュリティテスト
mvn quarkus:dev > quarkus.log 2>&1 & QUARKUS_PID=$!; sleep 10; wapiti -u http://localhost:8080 -f json -o ./target/wapiti.json; kill $QUARKUS_PID

# テストレポートの作成
./mvnw exec:exec@generate-report
```

---

## 注意事項

- **クラスター間 Kafka**: `shop-asite-inventory-in` は a-cluster から MirrorMaker2 でミラーリング。
- **在庫スキーマ**: `droneshop.inventory` テーブルを使用。Flyway で自動マイグレーション。
- **b-cluster デプロイ**: RHDH の Kubernetes タブで b-cluster のポッド状態を確認できます。
