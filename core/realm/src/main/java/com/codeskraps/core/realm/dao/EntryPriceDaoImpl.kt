package com.codeskraps.core.realm.dao

import com.codeskraps.core.realm.model.EntryPriceEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class EntryPriceDaoImpl @Inject constructor(
    r: Realm
) : EntryPriceDao {
    override val realm: Realm = r
    override val clazz: KClass<EntryPriceEntity> = EntryPriceEntity::class
}