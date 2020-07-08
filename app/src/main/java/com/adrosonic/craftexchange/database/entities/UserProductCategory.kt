package com.adrosonic.craftexchange.database.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserProductCategory : RealmObject(){
    @PrimaryKey
    var _id: Long?=0
    var id: Long? = 0
    var userid: Long ? = 0
    var productCategoryid : Long ? = 0
}