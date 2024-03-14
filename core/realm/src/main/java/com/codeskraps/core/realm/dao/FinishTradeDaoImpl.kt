package com.codeskraps.core.realm.dao

import com.codeskraps.core.realm.model.FinishedTradeEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class FinishTradeDaoImpl @Inject constructor(
    r: Realm
) : FinishTradeDao {
    override val realm: Realm = r
    override val clazz: KClass<FinishedTradeEntity> = FinishedTradeEntity::class
}