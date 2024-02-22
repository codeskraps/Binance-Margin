package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.TradeEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class TradeDaoImpl @Inject constructor(
    r: Realm
) : TradeDao {
    override val realm: Realm = r
    override val clazz: KClass<TradeEntity> = TradeEntity::class
}