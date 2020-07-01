package com.adrosonic.craftexchange.database.predicates

import com.adrosonic.craftexchange.database.entities.CraftUser
import com.adrosonic.craftexchange.repository.data.loginResponse.artisan.ArtisanResponse
import com.adrosonic.craftexchange.repository.data.loginResponse.buyer.BuyerResponse
import io.realm.Realm
import java.lang.Exception

class UserPredicates{
    companion object {
        var nextID : Long? = 0

        fun insertBuyer(userData : BuyerResponse) : Long? {
            nextID=0L
            val realm = Realm.getDefaultInstance()
            var user = userData.data.user

            realm!!.executeTransaction {

                var userObj =realm.where(CraftUser::class.java)
                    .equalTo("id", userData.data.user.id)
                    .limit(1)
                    .findFirst()

//                var uniqueId = try { userObj!!.id?:"" }catch (e: Exception){ e.printStackTrace() }

                if(userObj == null) {
                    var primId = it.where(CraftUser::class.java).max("_id")
                    if (primId == null) {
                        nextID = 1
                    } else {
                        nextID = primId.toLong() + 1
                    }
                    var exUser = it.createObject(CraftUser::class.java, nextID)
                    exUser.id = user.id
                    exUser.acctoken = userData.data.acctoken
                    exUser.firstName = user.firstName
                    exUser.lastName = user.lastName
                    exUser.email = user.email
                    exUser.designation = user.designation
                    exUser.mobile = user.mobile
                    exUser.alternateMobile = user.alternateMobile
                    exUser.pancard = user.pancard
                    exUser.websiteLink = user.websiteLink
                    exUser.socialMediaLink = user.socialMediaLink
                    exUser.poc_id = user.pointOfContact.id
                    exUser.poc_firstName = user.pointOfContact.firstName
                    exUser.poc_lastName = user.pointOfContact.lastName
                    exUser.poc_email = user.pointOfContact.email
                    exUser.poc_contactNo = user.pointOfContact.contactNo
                    exUser.companyName = user.companyDetails.companyName
                    exUser.company_id = user.companyDetails.id
                    exUser.contact = user.companyDetails.contact
                    exUser.logo = user.companyDetails.logo
                    exUser.cin = user.companyDetails.cin
                    exUser.gstNo = user.companyDetails.gstNo
                    exUser.weaverDetails = user.weaverDetails
                    exUser.refRoleId = user.refRoleId
                    exUser.registeredOn = user.registeredOn
                    exUser.status = user.status
                    exUser.emailVerified = user.emailVerified
                    exUser.lastLoggedIn = user.lastLoggedIn
//                    exUser.address = user.addressses.get() //TODO : Add multiple address
                    exUser.username = user.username
                    exUser.enabled = user.enabled
                    exUser.authorities = user.authorities
                    exUser.accountNonExpired = user.accountNonExpired
                    exUser.accountNonLocked = user.accountNonLocked
                    exUser.credentialsNonExpired = user.credentialsNonExpired

                    realm.copyToRealmOrUpdate(exUser)

                }else{
                    nextID=userObj?._id?:0
                    userObj?.id = user.id
                    userObj?.acctoken = userData.data.acctoken
                    userObj?.firstName = user.firstName
                    userObj?.lastName = user.lastName
                    userObj?.email = user.email
                    userObj?.designation = user.designation
                    userObj?.mobile = user.mobile
                    userObj?.alternateMobile = user.alternateMobile
                    userObj?.pancard = user.pancard
                    userObj?.websiteLink = user.websiteLink
                    userObj?.socialMediaLink = user.socialMediaLink
                    userObj?.poc_id = user.pointOfContact.id
                    userObj?.poc_firstName = user.pointOfContact.firstName
                    userObj?.poc_lastName = user.pointOfContact.lastName
                    userObj?.poc_email = user.pointOfContact.email
                    userObj?.poc_contactNo = user.pointOfContact.contactNo
                    userObj?.companyName = user.companyDetails.companyName
                    userObj?.company_id = user.companyDetails.id
                    userObj?.contact = user.companyDetails.contact
                    userObj?.logo = user.companyDetails.logo
                    userObj?.cin = user.companyDetails.cin
                    userObj?.gstNo = user.companyDetails.gstNo
                    userObj?.weaverDetails = user.weaverDetails
                    userObj?.refRoleId = user.refRoleId
                    userObj?.registeredOn = user.registeredOn
                    userObj?.status = user.status
                    userObj?.emailVerified = user.emailVerified
                    userObj?.lastLoggedIn = user.lastLoggedIn
//                    userObj?.address = user.addressses.get() //TODO : Add multiple address
                    userObj?.username = user.username
                    userObj?.enabled = user.enabled
                    userObj?.authorities = user.authorities
                    userObj?.accountNonExpired = user.accountNonExpired
                    userObj?.accountNonLocked = user.accountNonLocked
                    userObj?.credentialsNonExpired = user.credentialsNonExpired

                    realm.copyToRealmOrUpdate(userObj)
                }
            }
            return nextID!!
        }

        fun insertArtisan(userData : ArtisanResponse) : Long? {
            nextID=0L
            val realm = Realm.getDefaultInstance()
            var user = userData.data.user

            realm!!.executeTransaction {

                var userObj =realm.where(CraftUser::class.java)
                    .equalTo("id", userData.data.user.id)
                    .limit(1)
                    .findFirst()

//                var uniqueId = try { userObj!!.id?:"" }catch (e: Exception){ e.printStackTrace() }

                if(userObj == null) {
                    var primId = it.where(CraftUser::class.java).max("_id")
                    if (primId == null) {
                        nextID = 1
                    } else {
                        nextID = primId.toLong() + 1
                    }
                    var exUser = it.createObject(CraftUser::class.java, nextID)
                    exUser.id = user.id
                    exUser.acctoken = userData.data.acctoken
                    exUser.firstName = user.firstName
                    exUser.lastName = user.lastName
                    exUser.email = user.email
//                    exUser.designation = user.designation
                    exUser.mobile = user.mobile
                    exUser.alternateMobile = user.alternateMobile
                    exUser.pancard = user.pancard
//                    exUser.websiteLink = user.websiteLink
//                    exUser.socialMediaLink = user.socialMediaLink
//                    exUser.poc_id = user.pointOfContact.id
//                    exUser.poc_firstName = user.pointOfContact.firstName
//                    exUser.poc_lastName = user.pointOfContact.lastName
//                    exUser.poc_email = user.pointOfContact.email
//                    exUser.poc_contactNo = user.pointOfContact.contactNo
//                    exUser.companyName = user.companyDetails.companyName
//                    exUser.company_id = user.companyDetails.id
//                    exUser.contact = user.companyDetails.contact
//                    exUser.logo = user.companyDetails.logo
//                    exUser.cin = user.companyDetails.cin
//                    exUser.gstNo = user.companyDetails.gstNo
                    exUser.weaverDetails = user.weaverDetails
                    exUser.clusterid = user.cluster.id.toString()
                    exUser.clusterdesc = user.cluster.desc
                    exUser.refRoleId = user.refRoleId
                    exUser.registeredOn = user.registeredOn
                    exUser.status = user.status
                    exUser.emailVerified = user.emailVerified
                    exUser.lastLoggedIn = user.lastLoggedIn
//                    exUser.address = user.addressses.get() //TODO : Add multiple address
                    exUser.username = user.username
                    exUser.enabled = user.enabled
                    exUser.authorities = user.authorities
                    exUser.accountNonExpired = user.accountNonExpired
                    exUser.accountNonLocked = user.accountNonLocked
                    exUser.credentialsNonExpired = user.credentialsNonExpired

//                    realm.copyToRealmOrUpdate(exUser)

                }else{
                    nextID=userObj?._id?:0
                    userObj?.id = user.id
                    userObj?.acctoken = userData.data.acctoken
                    userObj?.firstName = user.firstName
                    userObj?.lastName = user.lastName
                    userObj?.email = user.email
//                    userObj?.designation = user.designation
                    userObj?.mobile = user.mobile
                    userObj?.alternateMobile = user.alternateMobile
                    userObj?.pancard = user.pancard
//                    userObj?.websiteLink = user.websiteLink
//                    userObj?.socialMediaLink = user.socialMediaLink
//                    userObj?.poc_id = user.pointOfContact.id
//                    userObj?.poc_firstName = user.pointOfContact.firstName
//                    userObj?.poc_lastName = user.pointOfContact.lastName
//                    userObj?.poc_email = user.pointOfContact.email
//                    userObj?.poc_contactNo = user.pointOfContact.contactNo
//                    userObj?.companyName = user.companyDetails.companyName
//                    userObj?.company_id = user.companyDetails.id
//                    userObj?.contact = user.companyDetails.contact
//                    userObj?.logo = user.companyDetails.logo
//                    userObj?.cin = user.companyDetails.cin
//                    userObj?.gstNo = user.companyDetails.gstNo
                    userObj?.weaverDetails = user.weaverDetails
                    userObj?.clusterid = user.cluster.id.toString()
                    userObj?.clusterdesc = user.cluster.desc
                    userObj?.refRoleId = user.refRoleId
                    userObj?.registeredOn = user.registeredOn
                    userObj?.status = user.status
                    userObj?.emailVerified = user.emailVerified
                    userObj?.lastLoggedIn = user.lastLoggedIn
//                    userObj?.address = user.addressses.get() //TODO : Add multiple address
                    userObj?.username = user.username
                    userObj?.enabled = user.enabled
                    userObj?.authorities = user.authorities
                    userObj?.accountNonExpired = user.accountNonExpired
                    userObj?.accountNonLocked = user.accountNonLocked
                    userObj?.credentialsNonExpired = user.credentialsNonExpired

                    realm.copyToRealmOrUpdate(userObj)
                }
            }
            return nextID!!
        }

        fun findUser(userid : Long) : CraftUser? {
            var realm = Realm.getDefaultInstance()
            var user : CraftUser ?= null
            try {
                user = realm.where(CraftUser::class.java)
                    .equalTo("id", userid)
                    .findFirst()
            } catch (e: Exception) {
                e.printStackTrace()
                //print logs
            } finally {
//                realm.close()
            }
            return user
        }

        fun insertAllCountries(){
            var realm = Realm.getDefaultInstance()
            try {
                realm?.executeTransaction {

                }
            }catch(e : Exception){
            }finally{
//                realm.close()
            }
        }

    }
}