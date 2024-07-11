package com.codeskraps.core.realm.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.codeskraps.core.realm.dao.TradeDao
import com.codeskraps.core.realm.model.TradeEntity

class TradesPagingSource(
    private val tradeDao: TradeDao
) : PagingSource<Int, TradeEntity>() {
    override fun getRefreshKey(state: PagingState<Int, TradeEntity>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TradeEntity> {
        return runCatching {
            val trades = tradeDao.findAll().sortedBy { it.time }.reversed()
            val currentPage = params.key ?: 1
            LoadResult.Page(
                data = trades,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (trades.isEmpty()) null else currentPage + 1
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }
}