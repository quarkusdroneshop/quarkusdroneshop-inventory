package io.quarkusdroneshop.inventory;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * ArchUnit によるアーキテクチャ適合性テスト。
 * パッケージ構造:
 *   io.quarkusdroneshop.inventory.domain.*         - ドメイン層 (エンティティ / イベント / コマンド)
 *   io.quarkusdroneshop.inventory.domain.json.*    - ※非推奨: Deserializer が domain 内に混在 → 要 infrastructure 移行
 *   io.quarkusdroneshop.inventory.domain.events.*  - ドメインイベント
 *   io.quarkusdroneshop.inventory.infrastructure.* - インフラ層 (Kafka / DB)
 */
@AnalyzeClasses(
        packages = "io.quarkusdroneshop.inventory",
        importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    // =========================================================================
    // 1. 命名規則
    // =========================================================================

    @ArchTest
    static final ArchRule Deserializer命名規則 =
        classes()
            .that().implement("org.apache.kafka.common.serialization.Deserializer")
            .or().areAssignableTo(
                io.quarkus.kafka.client.serialization.ObjectMapperDeserializer.class)
            .or().areAssignableTo(
                io.quarkus.kafka.client.serialization.JsonbDeserializer.class)
            .should().haveSimpleNameEndingWith("Deserializer");

    @ArchTest
    static final ArchRule 例外クラスの命名規則 =
        classes()
            .that().areAssignableTo(Exception.class)
            .and().resideInAPackage("io.quarkusdroneshop.inventory..")
            .should().haveSimpleNameEndingWith("Exception");

    // =========================================================================
    // 2. パッケージ配置ルール
    // =========================================================================

    /**
     * Deserializer は infrastructure に配置すること。
     * domain.json.RestockItemCommandDeserializer は違反 → infrastructure へ移行してください。
     */
    @ArchTest
    static final ArchRule Deserializerはinfrastructureに配置 =
        classes()
            .that().haveSimpleNameEndingWith("Deserializer")
            .should().resideInAPackage("..infrastructure..");

    // =========================================================================
    // 3. レイヤー間依存ルール
    // =========================================================================

    @ArchTest
    static final ArchRule ドメイン層はJAX_RSを使用しない =
        noClasses()
            .that().resideInAPackage("io.quarkusdroneshop.inventory.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("jakarta.ws.rs..");

    @ArchTest
    static final ArchRule ドメイン層はInfrastructureに依存しない =
        noClasses()
            .that().resideInAPackage("io.quarkusdroneshop.inventory.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("io.quarkusdroneshop.inventory.infrastructure..");

    @ArchTest
    static final ArchRule コマンドはInfrastructureに依存しない =
        noClasses()
            .that().haveSimpleNameEndingWith("Command")
            .and().resideInAPackage("io.quarkusdroneshop.inventory.domain..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule ドメインクラスはPublic =
        classes()
            .that().resideInAPackage("io.quarkusdroneshop.inventory.domain")
            .and().areNotInterfaces()
            .should().bePublic();

    @ArchTest
    static final ArchRule Infrastructureの依存範囲チェック =
        classes()
            .that().resideInAPackage("io.quarkusdroneshop.inventory.infrastructure..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "io.quarkusdroneshop.inventory.infrastructure..",
                "io.quarkusdroneshop.inventory.domain..",
                "java..",
                "javax..",
                "jakarta..",
                "io.quarkus..",
                "io.smallrye..",
                "org.eclipse.microprofile..",
                "org.apache.kafka..",
                "com.fasterxml..",
                "org.slf4j..",
                "org.jboss..");

    // =========================================================================
    // 4. 循環依存
    // =========================================================================

    @ArchTest
    static final ArchRule パッケージ間循環依存なし =
        slices()
            .matching("io.quarkusdroneshop.inventory.(*)..")
            .should().beFreeOfCycles();
}
