package com.codeskraps.core.domain.usecases

import com.codeskraps.core.domain.mappers.toMarginAccount
import com.codeskraps.core.domain.mappers.toMarginAccountEntity
import com.codeskraps.core.realm.MarginAccountDao
import com.codeskraps.core.realm.model.MarginAccountEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetMarginAccountUseCase @Inject constructor(
    private val marginAccountDao: com.codeskraps.core.realm.MarginAccountDao,
    private val client: com.codeskraps.core.client.BinanceClient
) {

    suspend operator fun invoke() = flow {
        coroutineScope {
            launch {
                client.marginAccount()?.let { acc ->
                    marginAccountDao.update(acc.toMarginAccountEntity())
                }
            }
            marginAccountDao.stream().collect { result: ResultsChange<com.codeskraps.core.realm.model.MarginAccountEntity> ->
                if (result.list.isNotEmpty()) {
                    emit(result.list.first().toMarginAccount())
                }
            }
        }
    }
}