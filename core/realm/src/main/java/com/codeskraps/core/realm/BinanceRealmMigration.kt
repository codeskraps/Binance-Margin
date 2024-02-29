package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.TradeEntity
import io.realm.kotlin.migration.AutomaticSchemaMigration


class BinanceRealmMigration : AutomaticSchemaMigration {
    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        if (migrationContext.newRealm.configuration.schemaVersion <= 3L) {
            migrationContext.newRealm.delete(TradeEntity::class.simpleName!!)
        }
    }

}