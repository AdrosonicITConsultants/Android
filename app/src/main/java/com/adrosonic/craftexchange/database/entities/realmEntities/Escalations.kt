package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Escalations : RealmObject(){
    @PrimaryKey
    var _id: Long?=0
    var escalationId : Long ?= 0L
    var enquiryId: Long ?= 0L
    var escalationFrom: Long ?= 0L
    var escalationTo: Long ?= 0L
    var category: Long ?= 0L
    var text: String ?= ""
    var isManual: Long ?= 0L
    var escalationToadmin: Long ?= 0L
    var escalationToadminDatetime: String ?= ""
    var isResolve: Long?= 0L
    var resolvedOn: String?= ""
    var autoEscalationTypeId: Long?= 0L
    var createdOn: String?= ""
    var modifiedOn: String?= ""

    companion object{
        const val COLUMN__ID = "_id"
        const val COLUMN_ENQUIRY_ID = "enquiryId"
        const val COLUMN_ESCALATION_ID = "escalationId"
        const val COLUMN_MODIFIED_ON = "modifiedOn"
        const val COLUMN_IS_RESOLVE = "isResolve"

    }
}