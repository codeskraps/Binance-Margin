package com.codeskraps.core.client.di

import com.binance.connector.client.SpotClient
import com.binance.connector.client.impl.SpotClientImpl
import com.codeskraps.core.client.BinanceStore
import com.codeskraps.core.client.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesSpotClient(
        store: BinanceStore
    ): SpotClient {
        return SpotClientImpl(
            store.apiKey,
            store.secretKey,
            BuildConfig.BASE_URL
        )
    }
}