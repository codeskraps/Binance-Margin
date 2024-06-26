package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.EntryPriceEntity
import com.codeskraps.core.realm.model.FinishedTradeEntity
import com.codeskraps.core.realm.model.OrderEntity
import com.codeskraps.core.realm.model.TradeEntity
import com.codeskraps.core.realm.model.TransferEntity
import io.realm.kotlin.migration.AutomaticSchemaMigration


class BinanceRealmMigration : AutomaticSchemaMigration {
    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        if (migrationContext.newRealm.configuration.schemaVersion <= 3L) {
            migrationContext.newRealm.delete(TradeEntity::class.simpleName!!)
        } else if (migrationContext.newRealm.configuration.schemaVersion <= 6L) {
            migrationContext.newRealm.delete(TransferEntity::class.simpleName!!)
        } else if (migrationContext.newRealm.configuration.schemaVersion <= 10L) {
            migrationContext.newRealm.delete(FinishedTradeEntity::class.simpleName!!)
        } else if (migrationContext.newRealm.configuration.schemaVersion <= 11L) {
            migrationContext.newRealm.delete(OrderEntity::class.simpleName!!)
        } else if (migrationContext.newRealm.configuration.schemaVersion <= 12L) {
            migrationContext.newRealm.delete(EntryPriceEntity::class.simpleName!!)
        }
    }
}