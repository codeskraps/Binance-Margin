package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.PnLEntity

interface PnLDao : RealmDao<PnLEntity> {

    fun findByTime(time: Long): List<PnLEntity>
}