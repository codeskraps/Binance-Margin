package com.codeskraps.core.realm.di

import com.codeskraps.core.realm.dao.EntryPriceDao
import com.codeskraps.core.realm.dao.EntryPriceDaoImpl
import com.codeskraps.core.realm.dao.FinishTradeDao
import com.codeskraps.core.realm.dao.FinishTradeDaoImpl
import com.codeskraps.core.realm.dao.MarginAccountDao
import com.codeskraps.core.realm.dao.MarginAccountDaoImpl
import com.codeskraps.core.realm.dao.OrderDao
import com.codeskraps.core.realm.dao.OrderDaoImpl
import com.codeskraps.core.realm.dao.PnLDailyDao
import com.codeskraps.core.realm.dao.PnLDailyDaoImpl
import com.codeskraps.core.realm.dao.PnLHourlyDao
import com.codeskraps.core.realm.dao.PnLHourlyDaoImpl
import com.codeskraps.core.realm.dao.TradeDao
import com.codeskraps.core.realm.dao.TradeDaoImpl
import com.codeskraps.core.realm.dao.TransferDao
import com.codeskraps.core.realm.dao.TransferDaoImpl
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

    @Binds
    fun bindTransferDao(impl: TransferDaoImpl): TransferDao

    @Binds
    fun bindFinishedTradeDao(impl: FinishTradeDaoImpl): FinishTradeDao

    @Binds
    fun bindsEntryPriceDao(impl: EntryPriceDaoImpl): EntryPriceDao
}