package com.codeskraps.core.domain.usecases.account

import com.codeskraps.core.domain.mappers.toMarginAccount
import com.codeskraps.core.domain.mappers.toMarginAccountEntity
import com.codeskraps.core.domain.model.MarginAccount
import com.codeskraps.core.realm.MarginAccountDao
import com.codeskraps.core.realm.model.MarginAccountEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMarginAccountUseCase @Inject constructor(
    private val marginAccountDao: MarginAccountDao,
    private val client: com.codeskraps.core.client.BinanceClient
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<MarginAccount> {
        return flowOf(clientFlow(), daoFlow()).flattenMerge()
    }

    private fun clientFlow() = flow<MarginAccount> {
        client.marginAccount()?.let { acc ->
            marginAccountDao.update(acc.toMarginAccountEntity())
        }
    }

    private suspend fun daoFlow(): Flow<MarginAccount> {
        return marginAccountDao.stream().map { result: ResultsChange<MarginAccountEntity> ->
            if (result.list.isNotEmpty()) {
                result.list.first().toMarginAccount()
            } else {
                MarginAccount()
            }
        }
    }
}