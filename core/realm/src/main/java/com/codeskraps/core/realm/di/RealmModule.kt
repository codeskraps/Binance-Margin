package com.codeskraps.core.realm.di

import com.codeskraps.core.realm.model.AssetEntity
import com.codeskraps.core.realm.model.MarginAccountEntity
import com.codeskraps.core.realm.model.OrderEntity
import com.codeskraps.core.realm.model.PnLHourlyEntity
import com.codeskraps.core.realm.model.PnlDailyEntity
import com.codeskraps.core.realm.model.TradeEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val realmConfig = RealmConfiguration.create(
            schema = setOf(
                MarginAccountEntity::class,
                AssetEntity::class,
                PnLHourlyEntity::class,
                PnlDailyEntity::class,
                TradeEntity::class,
                OrderEntity::class
            ),
        )
        return Realm.open(realmConfig)
    }
}