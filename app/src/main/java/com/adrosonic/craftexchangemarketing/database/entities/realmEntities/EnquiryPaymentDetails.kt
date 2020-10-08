package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class EnquiryPaymentDetails : RealmObject(){
    @PrimaryKey
    var _id: Long?=0

    var id: Long ?= 0
    var userid: Long ?=0
    var accNoUPIMobile: String? = ""
    var name: String? = ""
    var bankName: String? = ""
    var ifsc: String? = ""
    var branch: String? = ""
    var accountid: Long ?=0
    var accountDesc: String ?= ""

    companion object{
        const val COLUMN_ID = "id"
        const val COLUMN__ID = "_id"
    }
}