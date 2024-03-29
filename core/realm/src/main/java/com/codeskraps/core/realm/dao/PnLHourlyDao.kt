package com.codeskraps.core.realm.dao

import com.codeskraps.core.realm.model.PnLHourlyEntity

interface PnLHourlyDao : RealmDao<PnLHourlyEntity> {

    fun findByTime(time: Long): List<PnLHourlyEntity>

    suspend fun deleteOlder(time: Long)
}