package com.codeskraps.core.realm.dao

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.codeskraps.core.realm.model.TradeEntity
import com.codeskraps.core.realm.paging.TradesPagingSource
import io.realm.kotlin.Realm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.reflect.KClass

class TradeDaoImpl @Inject constructor(
    r: Realm
) : TradeDao {
    override val realm: Realm = r
    override val clazz: KClass<TradeEntity> = TradeEntity::class

    override fun getPagingTrades(): Flow<PagingData<TradeEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { TradesPagingSource(this) }
        ).flow
    }
}