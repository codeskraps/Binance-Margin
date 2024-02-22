package com.codeskraps.core.realm.di

import com.codeskraps.core.realm.MarginAccountDao
import com.codeskraps.core.realm.MarginAccountDaoImpl
import com.codeskraps.core.realm.PnLDao
import com.codeskraps.core.realm.PnLDaoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DaoModule {

    @Binds
    abstract fun bindUserDao(impl: MarginAccountDaoImpl): MarginAccountDao

    @Binds
    abstract fun bindPnLDao(impl: PnLDaoImpl): PnLDao
}