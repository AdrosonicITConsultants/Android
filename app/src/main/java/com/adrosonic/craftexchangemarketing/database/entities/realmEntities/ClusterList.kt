package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ClusterList : RealmObject() {
    @PrimaryKey
    var _id: Long?=0
    var clusterid: Long ?= 0
    var cluster: String ?=""
    var adjective: String ?=""
}