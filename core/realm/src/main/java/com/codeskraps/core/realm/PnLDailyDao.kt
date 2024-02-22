package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.PnlDailyEntity

interface PnLDailyDao : RealmDao<PnlDailyEntity> {

    fun findByTime(time: Long): List<PnlDailyEntity>
}