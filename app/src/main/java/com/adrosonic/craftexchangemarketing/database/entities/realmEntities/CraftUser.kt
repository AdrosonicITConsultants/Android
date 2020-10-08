package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

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
    var profilePic: String ?=""
    var alternateMobile: String ?=""
    var pancard: String ?=""
    var websiteLink: String ?=""
    var socialMediaLink: String ?=""
    var poc_id: Long?=0
    var poc_firstName: String ?=""
    var poc_lastName: String ?=""
    var poc_email: String ?=""
    var poc_contactNo: String ?=""
    var companyid: Long?=0
    var companyName: String ?=""
    var companyDesc: String ?=""
    var contact: String ?=""
    var brandLogo: String ?=""
    var cin: String ?=""
    var gstNo: String ?=""
    var weaverDetails: String ?=""
    var clusterid: String ?=""
    var clusterdesc: String?=""
    var clusteradjective: String?=""
    var refRoleId: Long ?=0
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
    var rating : String?=""
    var paymentAccountDetails : String ?=""
}
