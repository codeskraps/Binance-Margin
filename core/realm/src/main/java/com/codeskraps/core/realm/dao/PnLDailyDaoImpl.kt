package com.codeskraps.core.realm.dao

import com.codeskraps.core.realm.model.PnlDailyEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class PnLDailyDaoImpl @Inject constructor(
    r: Realm
) : PnLDailyDao {
    override val realm: Realm = r
    override val clazz: KClass<PnlDailyEntity> = PnlDailyEntity::class

    override fun findByTime(time: Long): List<PnlDailyEntity> {
        return realm.query(clazz, "time > $0", time).find()
    }
}