package com.codeskraps.core.domain.usecases.trade

import com.codeskraps.core.client.BinanceClient
import com.codeskraps.core.domain.mappers.toTransfer
import com.codeskraps.core.domain.mappers.toTransferEntity
import com.codeskraps.core.domain.model.Transfer
import com.codeskraps.core.realm.dao.TransferDao
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransfersUseCase @Inject constructor(
    private val client: BinanceClient,
    private val transferDao: TransferDao
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(network: (Boolean) -> Unit): Flow<List<Transfer>> {
        return flowOf(clientFlow(network), daoFlow()).flattenMerge()
    }

    private fun clientFlow(network: (Boolean) -> Unit): Flow<List<Transfer>> =
        flow {
            client.transfers().let { transfers ->
                transfers.map { it.toTransferEntity() }.forEach { transfer ->
                    val found = transferDao.findById(transfer.id)
                    if (found == null) {
                        if (transfer.asset == BinanceClient.BASE_ASSET) {
                            transfer.price = 1.0
                        }
                        transferDao.insert(transfer)
                    }
                }
                network(true)
            }
        }

    private suspend fun daoFlow(): Flow<List<Transfer>> = transferDao.stream().map { results ->
        results.list
    }.map { result ->
        result.map { it.toTransfer() }.sortedBy { it.timestamp }.reversed()
    }
}