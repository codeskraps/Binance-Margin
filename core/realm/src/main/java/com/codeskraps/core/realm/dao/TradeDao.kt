package com.codeskraps.core.realm.dao

import androidx.paging.PagingData
import com.codeskraps.core.realm.model.TradeEntity
import kotlinx.coroutines.flow.Flow

interface TradeDao : RealmDao<TradeEntity>{
    fun getPagingTrades(): Flow<PagingData<TradeEntity>>
}