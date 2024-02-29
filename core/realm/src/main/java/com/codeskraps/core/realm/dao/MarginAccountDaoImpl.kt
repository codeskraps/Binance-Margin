package com.codeskraps.core.realm.dao

import com.codeskraps.core.realm.model.MarginAccountEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class MarginAccountDaoImpl @Inject constructor(
    r: Realm
) : MarginAccountDao {

    override val realm: Realm = r
    override val clazz: KClass<MarginAccountEntity> = MarginAccountEntity::class
}