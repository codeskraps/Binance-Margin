package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.PnLEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class PnLDaoImpl @Inject constructor(
    r: Realm
) : PnLDao {
    override val realm: Realm = r
    override val clazz: KClass<PnLEntity> = PnLEntity::class

    override fun findByTime(time: Long): List<PnLEntity> {
        return realm.query(clazz, "time > $0", time).find()
    }
}