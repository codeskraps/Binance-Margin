package com.codeskraps.core.realm.dao

import com.codeskraps.core.realm.model.PnlDailyEntity

interface PnLDailyDao : RealmDao<PnlDailyEntity> {

    fun findByTime(time: Long): List<PnlDailyEntity>
}