package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.PnLHourlyEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class PnLHourlyDaoImpl @Inject constructor(
    r: Realm
) : PnLHourlyDao {
    override val realm: Realm = r
    override val clazz: KClass<PnLHourlyEntity> = PnLHourlyEntity::class

    override fun findByTime(time: Long): List<PnLHourlyEntity> {
        return realm.query(clazz, "time > $0", time).find()
    }

    override suspend fun deleteOlder(time: Long) {
        realm.write {
            val older = query(clazz, "time < $0", time).find()
            older.forEach { entity ->
                delete(entity)
            }
        }
    }
}