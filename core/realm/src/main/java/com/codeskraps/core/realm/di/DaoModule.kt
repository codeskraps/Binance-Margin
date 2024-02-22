package com.codeskraps.core.realm.di

import com.codeskraps.core.realm.MarginAccountDao
import com.codeskraps.core.realm.MarginAccountDaoImpl
import com.codeskraps.core.realm.OrderDao
import com.codeskraps.core.realm.OrderDaoImpl
import com.codeskraps.core.realm.PnLDailyDao
import com.codeskraps.core.realm.PnLDailyDaoImpl
import com.codeskraps.core.realm.PnLHourlyDao
import com.codeskraps.core.realm.PnLHourlyDaoImpl
import com.codeskraps.core.realm.TradeDao
import com.codeskraps.core.realm.TradeDaoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DaoModule {

    @Binds
    fun bindUserDao(impl: MarginAccountDaoImpl): MarginAccountDao

    @Binds
    fun bindPnLHourlyDao(impl: PnLHourlyDaoImpl): PnLHourlyDao

    @Binds
    fun bindPnLDailyDao(impl: PnLDailyDaoImpl): PnLDailyDao

    @Binds
    fun bindTradeDao(impl: TradeDaoImpl): TradeDao

    @Binds
    fun bindOrderDao(impl: OrderDaoImpl): OrderDao
}