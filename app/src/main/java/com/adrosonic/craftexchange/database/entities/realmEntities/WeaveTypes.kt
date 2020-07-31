package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WeaveTypes : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var productId : Long?=0
    var productWeaveId : Long?=0
    var weaveId : Long?=0     //TODO map with weave type desc
}