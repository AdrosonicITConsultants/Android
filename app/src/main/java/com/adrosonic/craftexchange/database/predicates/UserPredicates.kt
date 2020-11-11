package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.login.BuyerResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import io.realm.Realm
import java.lang.Exception

class UserPredicates{
    companion object {
        var nextID : Long? = 0

        fun insertBuyer(userData : BuyerResponse?) : Long? {
            nextID=0L
            val realm = CXRealmManager.getRealmInstance()
            var user = userData?.data?.user

            realm.executeTransaction {

                var userObj =realm.where(CraftUser::class.java)
                    .equalTo("id", userData?.data?.user?.id)
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
                    exUser.id = user?.id
                    exUser.acctoken = userData?.data?.acctoken
                    exUser.firstName = user?.firstName
                    exUser.lastName = user?.lastName
                    exUser.email = user?.email
                    exUser.designation = user?.designation
                    exUser.mobile = user?.mobile
                    exUser.alternateMobile = user?.alternateMobile
                    exUser.pancard = user?.pancard
                    exUser.websiteLink = user?.websiteLink
                    exUser.socialMediaLink = user?.socialMediaLink
                    exUser.clusterid = user?.cluster?.id.toString()
                    exUser.clusterdesc = user?.cluster?.desc
                    exUser.clusteradjective = user?.cluster?.adjective
                    exUser.poc_id = user?.pointOfContact?.id
                    exUser.poc_firstName = user?.pointOfContact?.firstName
                    exUser.poc_lastName = user?.pointOfContact?.lastName
                    exUser.poc_email = user?.pointOfContact?.email
                    exUser.poc_contactNo = user?.pointOfContact?.contactNo
                    exUser.companyName = user?.companyDetails?.companyName
                    exUser.companyid = user?.companyDetails?.id
                    exUser.contact = user?.companyDetails?.contact
                    exUser.brandLogo = user?.companyDetails?.logo
                    exUser.companyDesc = user?.companyDetails?.desc
                    exUser.cin = user?.companyDetails?.cin
                    exUser.gstNo = user?.companyDetails?.gstNo
                    exUser.weaverDetails = user?.weaverDetails
                    exUser.refRoleId = user?.refRoleId
                    exUser.registeredOn = user?.registeredOn
                    exUser.status = user?.status
                    exUser.emailVerified = user?.emailVerified
                    exUser.lastLoggedIn = user?.lastLoggedIn
                    exUser.rating = user?.rating
                    exUser.username = user?.username
                    exUser.enabled = user?.enabled
                    exUser.authorities = user?.authorities
                    exUser.accountNonExpired = user?.accountNonExpired
                    exUser.accountNonLocked = user?.accountNonLocked
                    exUser.credentialsNonExpired = user?.credentialsNonExpired

                    realm.copyToRealmOrUpdate(exUser)

                }else{
                    nextID= userObj._id ?:0
                    userObj.id = user?.id
                    userObj.acctoken = userData?.data?.acctoken
                    userObj.firstName = user?.firstName
                    userObj.lastName = user?.lastName
                    userObj.email = user?.email
                    userObj.designation = user?.designation
                    userObj.mobile = user?.mobile
                    userObj.alternateMobile = user?.alternateMobile
                    userObj.pancard = user?.pancard
                    userObj.websiteLink = user?.websiteLink
                    userObj.socialMediaLink = user?.socialMediaLink
                    userObj.clusterid = user?.cluster?.id.toString()
                    userObj.clusterdesc = user?.cluster?.desc
                    userObj.clusteradjective = user?.cluster?.adjective
                    userObj.poc_id = user?.pointOfContact?.id
                    userObj.poc_firstName = user?.pointOfContact?.firstName
                    userObj.poc_lastName = user?.pointOfContact?.lastName
                    userObj.poc_email = user?.pointOfContact?.email
                    userObj.poc_contactNo = user?.pointOfContact?.contactNo
                    userObj.companyName = user?.companyDetails?.companyName
                    userObj.companyid = user?.companyDetails?.id
                    userObj.contact = user?.companyDetails?.contact
                    userObj.brandLogo = user?.companyDetails?.logo
                    userObj.companyDesc = user?.companyDetails?.desc
                    userObj.cin = user?.companyDetails?.cin
                    userObj.gstNo = user?.companyDetails?.gstNo
                    userObj.weaverDetails = user?.weaverDetails
                    userObj.refRoleId = user?.refRoleId
                    userObj.registeredOn = user?.registeredOn
                    userObj.status = user?.status
                    userObj.emailVerified = user?.emailVerified
                    userObj.lastLoggedIn = user?.lastLoggedIn
                    userObj.rating = user?.rating
                    userObj.username = user?.username
                    userObj.enabled = user?.enabled
                    userObj.authorities = user?.authorities
                    userObj.accountNonExpired = user?.accountNonExpired
                    userObj.accountNonLocked = user?.accountNonLocked
                    userObj.credentialsNonExpired = user?.credentialsNonExpired

                    realm.copyToRealmOrUpdate(userObj)
                }
            }
            return nextID!!
        }

        fun insertArtisan(userData : ArtisanResponse?) : Long? {
            nextID=0L
            val realm = CXRealmManager.getRealmInstance()
            var user = userData?.data?.user

            realm.executeTransaction {

                var userObj =realm.where(CraftUser::class.java)
                    .equalTo("id", userData?.data?.user?.id)
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
                    exUser.id = user?.id
                    exUser.acctoken = userData?.data?.acctoken
                    exUser.firstName = user?.firstName
                    exUser.lastName = user?.lastName
                    exUser.email = user?.email
                    exUser.mobile = user?.mobile
                    exUser.profilePic = user?.profilePic
                    exUser.alternateMobile = user?.alternateMobile
                    exUser.pancard = user?.pancard

                    //Artisan Brand
                    exUser.companyName = user?.companyDetails?.companyName
                    exUser.companyid = user?.companyDetails?.id
                    exUser.contact = user?.companyDetails?.contact
                    exUser.brandLogo = user?.companyDetails?.logo
                    exUser.cin = user?.companyDetails?.cin
                    exUser.gstNo = user?.companyDetails?.gstNo
                    exUser.companyDesc = user?.companyDetails?.desc

                    exUser.weaverDetails = user?.weaverDetails?.weaverId
                    exUser.clusterid = user?.cluster?.id.toString()
                    exUser.clusterdesc = user?.cluster?.desc
                    exUser.clusteradjective = user?.cluster?.adjective
                    exUser.refRoleId = user?.refRoleId
                    exUser.registeredOn = user?.registeredOn
                    exUser.status = user?.status
                    exUser.emailVerified = user?.emailVerified
                    exUser.lastLoggedIn = user?.lastLoggedIn
                    exUser.username = user?.username
                    exUser.rating = user?.rating
//                    exUser.paymentAccountDetails = user.paymentAccountDetails  //TODO: to be implemented
                    exUser.enabled = user?.enabled
                    exUser.authorities = user?.authorities
                    exUser.accountNonExpired = user?.accountNonExpired
                    exUser.accountNonLocked = user?.accountNonLocked
                    exUser.credentialsNonExpired = user?.credentialsNonExpired

                    realm.copyToRealmOrUpdate(exUser)

                }else{
                    nextID= userObj._id ?:0
                    userObj.id = user?.id
                    userObj.acctoken = userData?.data?.acctoken
                    userObj.firstName = user?.firstName
                    userObj.lastName = user?.lastName
                    userObj.email = user?.email
                    userObj.mobile = user?.mobile
                    userObj.alternateMobile = user?.alternateMobile
                    userObj.pancard = user?.pancard
                    userObj.profilePic = user?.profilePic

                    //Artisan Brand
                    userObj.companyName = user?.companyDetails?.companyName
                    userObj.companyid = user?.companyDetails?.id
                    userObj.contact = user?.companyDetails?.contact
                    userObj.brandLogo = user?.companyDetails?.logo
                    userObj.cin = user?.companyDetails?.cin
                    userObj.gstNo = user?.companyDetails?.gstNo
                    userObj.companyDesc = user?.companyDetails?.desc

                    userObj.weaverDetails = user?.weaverDetails?.weaverId
                    userObj.clusterid = user?.cluster?.id.toString()
                    userObj.clusterdesc = user?.cluster?.desc
                    userObj.clusteradjective = user?.cluster?.adjective
                    userObj.refRoleId = user?.refRoleId
                    userObj.registeredOn = user?.registeredOn
                    userObj.status = user?.status
                    userObj.emailVerified = user?.emailVerified
                    userObj.lastLoggedIn = user?.lastLoggedIn
                    userObj.username = user?.username
                    userObj.rating = user?.rating
//                    userObj?.paymentAccountDetails = user.paymentAccountDetails  //TODO: to be implemented

                    userObj.enabled = user?.enabled
                    userObj.authorities = user?.authorities
                    userObj.accountNonExpired = user?.accountNonExpired
                    userObj.accountNonLocked = user?.accountNonLocked
                    userObj.credentialsNonExpired = user?.credentialsNonExpired

                    realm.copyToRealmOrUpdate(userObj)
                }
            }
            return nextID!!
        }

        fun findUser(userid : Long) : CraftUser? {
            val realm = CXRealmManager.getRealmInstance()
            var user : CraftUser?= null
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

        fun editBuyerDetails(userData : EditProfileResponse?) {
            val realm = CXRealmManager.getRealmInstance()
            var user = userData?.data
            realm.executeTransaction {
                var userObj =realm.where(CraftUser::class.java)
                    .equalTo("id", userData?.data?.id)
                    .limit(1)
                    .findFirst()

                userObj?.id = user?.id
//                userObj?.acctoken = userData.data.acctoken
                userObj?.firstName = user?.firstName
                userObj?.lastName = user?.lastName
                userObj?.email = user?.email
                userObj?.designation = user?.designation
                userObj?.mobile = user?.mobile
                userObj?.alternateMobile = user?.alternateMobile
                userObj?.pancard = user?.pancard
                userObj?.websiteLink = user?.websiteLink
                userObj?.socialMediaLink = user?.socialMediaLink
                userObj?.poc_id = user?.pointOfContact?.id
                userObj?.poc_firstName = user?.pointOfContact?.firstName
                userObj?.poc_lastName = user?.pointOfContact?.lastName
                userObj?.poc_email = user?.pointOfContact?.email
                userObj?.poc_contactNo = user?.pointOfContact?.contactNo
                userObj?.companyName = user?.companyDetails?.companyName
                userObj?.companyid = user?.companyDetails?.id
                userObj?.contact = user?.companyDetails?.contact
                userObj?.brandLogo = user?.companyDetails?.logo
                userObj?.cin = user?.companyDetails?.cin
                userObj?.gstNo = user?.companyDetails?.gstNo
//                userObj?.weaverDetails = user.weaverDetails
                userObj?.refRoleId = user?.refRoleId
                userObj?.registeredOn = user?.registeredOn
                userObj?.status = user?.status
                userObj?.emailVerified = user?.emailVerified
                userObj?.lastLoggedIn = user?.lastLoggedIn
//                    userObj?.address = user.addressses.get() //TODO : Add multiple address
                userObj?.username = user?.username
                userObj?.enabled = user?.enabled
                userObj?.authorities = user?.authorities
                userObj?.accountNonExpired = user?.accountNonExpired
                userObj?.accountNonLocked = user?.accountNonLocked
                userObj?.credentialsNonExpired = user?.credentialsNonExpired

                realm.copyToRealmOrUpdate(userObj!!)
            }
        }

        fun refreshArtisanDetails(userData : ProfileResponse?){
            val realm = CXRealmManager.getRealmInstance()
            var user = userData?.data?.user
            realm.executeTransaction {

                var userObj = realm.where(CraftUser::class.java)
                    .equalTo("id", userData?.data?.user?.id)
                    .limit(1)
                    .findFirst()
                Log.e("UserDetails",userObj.toString())
                try {

                    if(userObj != null){

                        userObj?.id = user?.id
                        //                userObj?.acctoken = userData.data.acctoken
                        userObj?.firstName = user?.firstName
                        userObj?.lastName = user?.lastName
                        userObj?.email = user?.email
                        userObj?.mobile = user?.mobile
                        userObj?.alternateMobile = user?.alternateMobile
                        userObj?.pancard = user?.pancard
                        userObj?.profilePic = user?.profilePic
                        userObj?.weaverDetails = user?.weaverDetails?.weaverId
                        userObj?.clusterid = user?.cluster?.id.toString()
                        userObj?.clusterdesc = user?.cluster?.desc
                        userObj?.clusteradjective = user?.cluster?.adjective
                        //                userObj?.refRoleId = user.refRoleId
                        userObj?.companyName = user?.companyDetails?.companyName
                        userObj?.companyid = user?.companyDetails?.id
                        userObj?.contact = user?.companyDetails?.contact
                        userObj?.brandLogo = user?.companyDetails?.logo
                        userObj?.cin = user?.companyDetails?.cin
                        userObj?.gstNo = user?.companyDetails?.gstNo
                        userObj?.companyDesc = user?.companyDetails?.desc
                        userObj?.registeredOn = user?.registeredOn
                        userObj?.status = user?.status
                        userObj?.emailVerified = user?.emailVerified
                        userObj?.lastLoggedIn = user?.lastLoggedIn
                        userObj?.username = user?.username
                        userObj?.rating = user?.rating
                        userObj?.enabled = user?.enabled
                        userObj?.authorities = user?.authorities
                        userObj?.accountNonExpired = user?.accountNonExpired
                        userObj?.accountNonLocked = user?.accountNonLocked
                        userObj?.credentialsNonExpired = user?.credentialsNonExpired

                        realm.copyToRealmOrUpdate(userObj!!)
                    }

                }catch (e:Exception){
                    Log.e("refresh artisan","$e")
                }
            }
        }

        fun refreshBuyerDetails(userData : ProfileResponse?){
            val realm = CXRealmManager.getRealmInstance()
            var user = userData?.data?.user
            realm.executeTransaction {

                var userObj = realm.where(CraftUser::class.java)
                    .equalTo("id", userData?.data?.user?.id)
                    .limit(1)
                    .findFirst()

                if(userObj != null){
                    userObj?.id = user?.id
//                userObj?.acctoken = userData.data.acctoken
                    userObj?.firstName = user?.firstName
                    userObj?.lastName = user?.lastName
                    userObj?.email = user?.email
                    userObj?.mobile = user?.mobile
                    userObj?.designation = user?.designation
                    userObj?.alternateMobile = user?.alternateMobile
                    userObj?.pancard = user?.pancard
                    userObj?.websiteLink = user?.websiteLink
                    userObj?.socialMediaLink = user?.socialMediaLink
                    userObj?.profilePic = user?.profilePic
//                userObj?.weaverDetails = user.weaverDetails.weaverId
                    userObj?.clusterid = user?.cluster?.id.toString()
                    userObj?.clusterdesc = user?.cluster?.desc
                    userObj?.clusteradjective = user?.cluster?.adjective
//                userObj?.refRoleId = user.refRoleId
                    userObj?.companyName = user?.companyDetails?.companyName
                    userObj?.companyid = user?.companyDetails?.id
                    userObj?.contact = user?.companyDetails?.contact
                    userObj?.brandLogo = user?.companyDetails?.logo
                    userObj?.cin = user?.companyDetails?.cin
                    userObj?.gstNo = user?.companyDetails?.gstNo
                    userObj?.companyDesc = user?.companyDetails?.desc
                    userObj?.poc_id = user?.pointOfContact?.id
                    userObj?.poc_firstName = user?.pointOfContact?.firstName
                    userObj?.poc_lastName = user?.pointOfContact?.lastName
                    userObj?.poc_email = user?.pointOfContact?.email
                    userObj?.poc_contactNo = user?.pointOfContact?.contactNo
                    userObj?.registeredOn = user?.registeredOn
                    userObj?.status = user?.status
                    userObj?.emailVerified = user?.emailVerified
                    userObj?.lastLoggedIn = user?.lastLoggedIn
                    userObj?.username = user?.username
                    userObj?.rating = user?.rating
                    userObj?.enabled = user?.enabled
                    userObj?.authorities = user?.authorities
                    userObj?.accountNonExpired = user?.accountNonExpired
                    userObj?.accountNonLocked = user?.accountNonLocked
                    userObj?.credentialsNonExpired = user?.credentialsNonExpired

                    realm.copyToRealmOrUpdate(userObj!!)
                }
            }
        }

        fun insertPaymentDetails(userData : ProfileResponse?): Long?{
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var paymentList = userData?.data?.user?.paymentAccountDetails
            try {
                realm.executeTransaction {
                    var payIterator = paymentList?.iterator()
                    if (payIterator != null) {
                        while (payIterator.hasNext()) {
                            var pay = payIterator.next()
                            var payObj = realm.where(PaymentAccount::class.java)
                                .equalTo("id", pay.id)
                                .limit(1)
                                .findFirst()

                            if (payObj == null) {
                                var primId = it.where(PaymentAccount::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var expay = it.createObject(PaymentAccount::class.java, nextID)

                                expay.id = pay.id
                                expay.userid = pay.userId
                                expay.accNoUPIMobile = pay.accNo_UPI_Mobile
                                expay.name = pay.name
                                expay.bankName = pay.bankName
                                expay.ifsc = pay.ifsc
                                expay.branch = pay.branch
                                expay.accountid = pay.accountType.id
                                expay.accountDesc = pay.accountType.accountDesc

                                realm.copyToRealmOrUpdate(expay)
                            }else{
                                nextID = payObj._id ?: 0

                                payObj.id = pay.id
                                payObj.userid = pay.userId
                                payObj.accNoUPIMobile = pay.accNo_UPI_Mobile
                                payObj.name = pay.name
                                payObj.bankName = pay.bankName
                                payObj.ifsc = pay.ifsc
                                payObj.branch = pay.branch
                                payObj.accountid = pay.accountType.id
                                payObj.accountDesc = pay.accountType.accountDesc

                                realm.copyToRealmOrUpdate(payObj)
                            }
                        }
                    }
                }
        }catch (e: Exception) {
            } finally {
//                realm.close()
            }
            return nextID
        }

        fun getPaymentDetails(userid: String,accountid : Long): PaymentAccount? {
            val realm = CXRealmManager.getRealmInstance()
            var payment : PaymentAccount?= null
            try {
                realm.executeTransaction {
                    payment = realm.where(PaymentAccount::class.java)
                        .equalTo("userid", userid.toLong())
                        .and()
                        .equalTo("accountid", accountid)
                        .findFirst()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
//                realm.close()
            }
            return payment
        }

        fun deleteData(){
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                try {
                    realm?.where(ArtisanProductCategory::class.java).findAll().deleteAllFromRealm()
                    realm?.where(ArtisanProducts::class.java).findAll().deleteAllFromRealm()
                    realm?.where(BrandList::class.java).findAll().deleteAllFromRealm()
                    realm?.where(BuyerCustomProduct::class.java).findAll().deleteAllFromRealm()
                    realm?.where(CategoryProducts::class.java).findAll().deleteAllFromRealm()
                    realm?.where(ClusterList::class.java).findAll().deleteAllFromRealm()
                    realm?.where(CompletedEnquiries::class.java).findAll().deleteAllFromRealm()
                    realm?.where(CraftUser::class.java).findAll().deleteAllFromRealm()
                    realm?.where(Enquiries::class.java).findAll().deleteAllFromRealm()
                    realm?.where(EnquiryPaymentDetails::class.java).findAll().deleteAllFromRealm()
                    realm?.where(Notifications::class.java).findAll().deleteAllFromRealm()
                    realm?.where(OngoingEnquiries::class.java).findAll().deleteAllFromRealm()
                    realm?.where(PaymentAccount::class.java).findAll().deleteAllFromRealm()
                    realm?.where(ProductCares::class.java).findAll().deleteAllFromRealm()
                    realm?.where(ProductCatalogue::class.java).findAll().deleteAllFromRealm()
                    realm?.where(ProductDimens::class.java).findAll().deleteAllFromRealm()
                    realm?.where(ProductImages::class.java).findAll().deleteAllFromRealm()
                    realm?.where(RelatedProducts::class.java).findAll().deleteAllFromRealm()
                    realm?.where(UserAddress::class.java).findAll().deleteAllFromRealm()
                    realm?.where(WeaveTypes::class.java).findAll().deleteAllFromRealm()
                    realm?.where(Moqs::class.java).findAll().deleteAllFromRealm()
                    realm?.where(EnquiryProductDetails::class.java).findAll().deleteAllFromRealm()
                    realm?.where(Transactions::class.java).findAll().deleteAllFromRealm()
                    realm?.where(Orders::class.java).findAll().deleteAllFromRealm()
                    realm?.where(QcDetails::class.java).findAll().deleteAllFromRealm()
                    realm?.where(ChangeRequests::class.java).findAll().deleteAllFromRealm()
                    realm?.where(TaxInvDetails::class.java).findAll().deleteAllFromRealm()
                    realm?.where(OrderProgressDetails::class.java).findAll().deleteAllFromRealm()
                    realm?.where(Escalations::class.java).findAll().deleteAllFromRealm()
                }catch (e:Exception){
                    Log.e("DeleteData","${e.printStackTrace()}")
                }
            }
        }

        fun insertAllCountries(){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {

                }
            }catch(e : Exception){
            }finally{
//                realm.close()
            }
        }

    }
}