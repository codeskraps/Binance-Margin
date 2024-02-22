package com.codeskraps.core.realm.di

import com.codeskraps.core.realm.model.AssetEntity
import com.codeskraps.core.realm.model.MarginAccountEntity
import com.codeskraps.core.realm.model.PnLEntity
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
                PnLEntity::class
            ),
        )
        return Realm.open(realmConfig)
    }
}