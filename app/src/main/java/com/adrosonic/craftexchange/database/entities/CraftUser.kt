package com.adrosonic.craftexchange.database.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CraftUser : RealmObject(){
    @PrimaryKey
    var _id: Long ?=0
    var id: Long ?=0
    var acctoken: String ?=""
    var firstName: String ?=""
    var lastName: String ?=""
    var email: String ?=""
    var designation: String ?=""
    var mobile: String ?=""
    var alternateMobile: String ?=""
    var pancard: String ?=""
    var websiteLink: String ?=""
    var socialMediaLink: String ?=""
    var poc_id: Int?=0
    var poc_firstName: String ?=""
    var poc_lastName: String ?=""
    var poc_email: String ?=""
    var poc_contactNo: String ?=""
    var company_id: Int?=0
    var companyName: String ?=""
    var contact: String ?=""
    var logo: String ?=""
    var cin: String ?=""
    var gstNo: String ?=""
    var weaverDetails: String ?=""
    var clusterid: String ?=""
    var clusterdesc: String?=""
    var refRoleId: Int ?=0
    var registeredOn: String ?=""
    var status: Int ?=0
    var emailVerified: Int ?=0
    var lastLoggedIn: String ?=""
    var address : String ?=""
    var username: String ?=""
    var enabled: Boolean ?= false
    var authorities: String ?=""
    var accountNonExpired: Boolean ?=false
    var accountNonLocked: Boolean ?=false
    var credentialsNonExpired: Boolean ?=false
}
