package com.codeskraps.core.client.di

import com.binance.connector.client.SpotClient
import com.binance.connector.client.impl.SpotClientImpl
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
    fun providesSpotClient(): SpotClient {
        return SpotClientImpl(
            BuildConfig.API_KEY,
            BuildConfig.SECRET_KEY,
            BuildConfig.BASE_URL
        )
    }
}