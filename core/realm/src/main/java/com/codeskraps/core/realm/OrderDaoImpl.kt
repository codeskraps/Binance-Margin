package com.codeskraps.core.realm

import com.codeskraps.core.realm.model.OrderEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class OrderDaoImpl @Inject constructor(
    r: Realm
) : OrderDao {
    override val realm: Realm = r
    override val clazz: KClass<OrderEntity> = OrderEntity::class
}