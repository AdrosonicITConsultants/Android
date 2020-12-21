package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserDatabase : RealmObject() {

    @PrimaryKey
    var _id: Long? = 0
    var id :Long? = 0
    var weaverId : String?=""
    var rating : Float?=0.0f
    var status : Int?=0
    var email : String?=""
    var cluster: String?=""
    var brandName : String?=""
    var firstName : String?=""
    var lastName : String?=""
    var mobile : String?=""
    var dateAdded :String?=""
    var isArtisan: Boolean?=true


    companion object {
        const val COLUMN_TABLE = "UserDatabase"
        const val COLUMN__ID = "_id"
        const val COLUMN_ID = "id"
        const val COLUMN_IS_ARTISAN = "isArtisan"
        const val COLUMN_DATE_ADDED = "dateAdded"
        const val COLUMN_FIRST_NAME = "firstName"
        const val COLUMN_LAST_NAME = "lastName"
        const val COLUMN_BRAND_NAME = "brandName"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_MOBILE = "mobile"
        const val COLUMN_CLUSTER = "cluster"
        const val COLUMN_RATING = "rating"
    }
}