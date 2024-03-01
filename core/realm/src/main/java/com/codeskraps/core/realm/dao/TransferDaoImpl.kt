package com.codeskraps.core.realm.dao

import com.codeskraps.core.realm.model.TransferEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class TransferDaoImpl @Inject constructor(
    r: Realm
) : TransferDao {
    override val realm: Realm = r
    override val clazz: KClass<TransferEntity> = TransferEntity::class
}