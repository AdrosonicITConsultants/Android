package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.*
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryResponse
import io.realm.RealmResults
import io.realm.Sort
import java.lang.Exception

class EnquiryPredicates {
    companion object {
        private var nextID: Long? = 0

        fun insertBuyerEnquiries(enquiryDetails : GenerateEnquiryResponse){
            val realm = CXRealmManager.getRealmInstance()
            var enquiry = enquiryDetails.data
            realm.executeTransaction{
                try {
                    var enqObj = realm.where(Enquiries::class.java)
                        .equalTo(Enquiries.COLUMN_ENQUIRY_ID, enquiry.enquiry.id)
                        .and()
                        .equalTo(Enquiries.COLUMN_PRODUCT_ID, enquiry.enquiry.productId)
                        .limit(1)
                        .findFirst()
                    if(enqObj==null){
                        var primId = it.where(Enquiries::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var exEnq = it.createObject(Enquiries::class.java, nextID)
                        exEnq?.id = enquiry.enquiry.id
                        exEnq?.code = enquiry.enquiry.code
                        exEnq?.generatedBy = enquiry.enquiry.generatedBy
                        exEnq?.productID = enquiry.enquiry.productId
                        exEnq?.customProductID = enquiry.enquiry.customProductId
                        exEnq?.enquiryStatus = enquiry.enquiry.id
                        exEnq?.artisanID = enquiry.enquiry.id
                        exEnq?.moqID = enquiry.enquiry.id
                        exEnq?.piID = enquiry.enquiry.id
                        exEnq?.enquiryOrderStageID = enquiry.enquiry.enquiryOrderStageId
                        exEnq?.createdOn = enquiry.enquiry.createdOn
                        exEnq?.modifiedOn = enquiry.enquiry.modifiedOn
                        exEnq?.productName = enquiry.productName
                        exEnq?.ifExists = enquiry.ifExists


                        realm.copyToRealmOrUpdate(exEnq)
                    }else{
                        nextID = enqObj._id ?: 0
                        enqObj.id = enquiry.enquiry.id
                        enqObj.id = enquiry.enquiry.id
                        enqObj.code = enquiry.enquiry.code
                        enqObj.generatedBy = enquiry.enquiry.generatedBy
                        enqObj.productID = enquiry.enquiry.productId
                        enqObj.customProductID = enquiry.enquiry.customProductId
                        enqObj.enquiryStatus = enquiry.enquiry.id
                        enqObj.artisanID = enquiry.enquiry.id
                        enqObj.moqID = enquiry.enquiry.id
                        enqObj.piID = enquiry.enquiry.id
                        enqObj.enquiryOrderStageID = enquiry.enquiry.enquiryOrderStageId
                        enqObj.createdOn = enquiry.enquiry.createdOn
                        enqObj.modifiedOn = enquiry.enquiry.modifiedOn
                        enqObj.productName = enquiry.productName
                        enqObj.ifExists = enquiry.ifExists

                        realm.copyToRealmOrUpdate(enqObj)
                    }
                }catch (e:Exception){
                    Log.e("InsertEnquiry",e.printStackTrace().toString())
                }
            }
        }

        fun getExistingEnquiryDetails(productId : Long, ifExists : Boolean) : Enquiries? {
            var realm = CXRealmManager.getRealmInstance()
            var enquiry : Enquiries?=null
            realm.executeTransaction{
                 enquiry = realm.where(Enquiries::class.java)
                    .equalTo(Enquiries.COLUMN_PRODUCT_ID,productId).and().equalTo(Enquiries.COLUMN_IF_EXISTS,ifExists)
                    .limit(1)
                    .findFirst()
            }
            return enquiry
        }

        fun updateIfExistEnquiry(productId: Long, enquirId : Long, ifExists : Boolean){
            var realm = CXRealmManager.getRealmInstance()
            var enquiry : Enquiries?=null
            realm.executeTransaction{
                enquiry = realm.where(Enquiries::class.java)
                    .equalTo(Enquiries.COLUMN_PRODUCT_ID,productId)
                    .and()
                    .equalTo(Enquiries.COLUMN_ENQUIRY_ID,enquirId)
                    .limit(1)
                    .findFirst()

                enquiry?.ifExists = ifExists
            }
        }

        fun insertOngoingEnquiries(enquiryDetails : EnquiryResponse?){
            val realm = CXRealmManager.getRealmInstance()
            var enquiryList = enquiryDetails?.data
            var enqItr = enquiryList?.iterator()
            realm.executeTransaction{
                try {
                    if(enqItr!=null) {
                        while (enqItr.hasNext()) {
                            var enquiry = enqItr.next()
                            var enqObj = realm.where(OngoingEnquiries::class.java)
                                .equalTo(
                                    OngoingEnquiries.COLUMN_ENQUIRY_ID,
                                    enquiry.openEnquiriesResponse?.enquiryId
                                )
                                .or()
                                .equalTo(OngoingEnquiries.COLUMN_ENQUIRY_CODE,enquiry?.openEnquiriesResponse?.enquiryCode)
                                .limit(1)
                                .findFirst()
                            if (enqObj == null) {
                                var primId = it.where(OngoingEnquiries::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }

                                var exEnq = it.createObject(OngoingEnquiries::class.java, nextID)
                                exEnq?.enquiryID = enquiry?.openEnquiriesResponse?.enquiryId
                                exEnq?.enquiryCode = enquiry?.openEnquiriesResponse?.enquiryCode
                                exEnq?.productID = enquiry?.openEnquiriesResponse?.productId
                                exEnq?.productName = enquiry?.openEnquiriesResponse?.productName
                                exEnq?.productCode = enquiry?.openEnquiriesResponse?.productCode
                                exEnq?.enquiryStatusID = enquiry?.openEnquiriesResponse?.enquiryStatusId
                                exEnq?.enquiryStageID = enquiry?.openEnquiriesResponse?.enquiryStageId
                                exEnq?.innerEnquiryStageID = enquiry?.openEnquiriesResponse?.innerEnquiryStageId
                                exEnq?.productStatusID = enquiry?.openEnquiriesResponse?.productStatusId

                                exEnq?.orderCode = enquiry?.openEnquiriesResponse?.orderCode
                                exEnq?.orderCreatedOn = enquiry?.openEnquiriesResponse?.orderCreatedOn
                                exEnq?.expectedDate = enquiry?.openEnquiriesResponse?.excpectedDate
                                exEnq?.startedOn = enquiry?.openEnquiriesResponse?.startedOn
                                exEnq?.lastUpdated = enquiry?.openEnquiriesResponse?.lastUpdated
                                exEnq?.totalAmount = enquiry?.openEnquiriesResponse?.totalAmount

                                exEnq?.isMoqSend = enquiry?.openEnquiriesResponse?.isMoqSend?:0
                                exEnq?.isPiSend = enquiry?.openEnquiriesResponse?.isPiSend?:0
                                exEnq?.changeRequestOn = enquiry?.openEnquiriesResponse?.changeRequestOn
                                exEnq?.isMoqRejected = enquiry?.isMoqRejected?:0
                                exEnq?.isBlue = enquiry?.isBlue

                                exEnq?.productType = enquiry?.openEnquiriesResponse?.productType
                                exEnq?.productCategoryID = enquiry?.openEnquiriesResponse?.productCategoryId
                                exEnq?.warpYarnID = enquiry?.openEnquiriesResponse?.warpYarnId
                                exEnq?.weftYarnID = enquiry?.openEnquiriesResponse?.weftYarnId
                                exEnq?.extraWeftYarnID = enquiry?.openEnquiriesResponse?.extraWeftYarnId
                                exEnq?.productImages = enquiry?.openEnquiriesResponse?.productImages
                                exEnq?.madeWithAnthran = enquiry?.openEnquiriesResponse?.isMoqSend
//                                exEnq?.brandName = enquiry?.brandName

                                exEnq?.historyProductID = enquiry?.openEnquiriesResponse?.historyProductId
                                exEnq?.productHistoryName = enquiry?.openEnquiriesResponse?.productHistoryName
                                exEnq?.productHistoryCode = enquiry?.openEnquiriesResponse?.productHistoryCode
                                exEnq?.productCategoryHistoryID = enquiry?.openEnquiriesResponse?.productCategoryHistoryId
                                exEnq?.productHistoryImages = enquiry?.openEnquiriesResponse?.productHistoryImages
                                exEnq?.warpYarnHistoryID = enquiry?.openEnquiriesResponse?.warpYarnHistoryId
                                exEnq?.weftYarnHistoryID = enquiry?.openEnquiriesResponse?.weftYarnHistoryId
                                exEnq?.extraWeftYarnHistoryID = enquiry?.openEnquiriesResponse?.extraWeftYarnHistoryId
                                exEnq?.productStatusHistoryID = enquiry?.openEnquiriesResponse?.productStatusHistoryId
                                exEnq?.madeWithAnthranHistory = enquiry?.openEnquiriesResponse?.madeWittAnthranHistory

                                exEnq?.userId = enquiry?.userId
                                exEnq?.clusterName = enquiry?.clusterName
                                exEnq?.firstName = enquiry?.openEnquiriesResponse?.firstName
                                exEnq?.lastName = enquiry?.openEnquiriesResponse?.lastName
                                exEnq?.brandDesc = enquiry?.openEnquiriesResponse?.description
                                exEnq?.profileImage = enquiry?.openEnquiriesResponse?.profilePic
                                exEnq?.alternateMobile = enquiry?.openEnquiriesResponse?.alternateMobile
//                                exEnq?.companyName = enquiry?.openEnquiriesResponse?.companyName
                                exEnq?.ProductBrandName = enquiry?.brandName //todo : to be changed
                                exEnq?.logo = enquiry?.openEnquiriesResponse?.logo
                                exEnq?.city = enquiry?.openEnquiriesResponse?.city
                                exEnq?.district = enquiry?.openEnquiriesResponse?.district
                                exEnq?.pincode = enquiry?.openEnquiriesResponse?.pincode
                                exEnq?.line1 = enquiry?.openEnquiriesResponse?.line1
                                exEnq?.line2 = enquiry?.openEnquiriesResponse?.line2
                                exEnq?.street = enquiry?.openEnquiriesResponse?.street
                                exEnq?.state = enquiry?.openEnquiriesResponse?.state
                                exEnq?.country = enquiry?.openEnquiriesResponse?.country
                                exEnq?.email = enquiry?.openEnquiriesResponse?.email
                                exEnq?.mobile = enquiry?.openEnquiriesResponse?.mobile
                                exEnq?.pocFirstName = enquiry?.openEnquiriesResponse?.pocFirstName
                                exEnq?.pocLastName = enquiry?.openEnquiriesResponse?.pocLastName
                                exEnq?.pocContact = enquiry?.openEnquiriesResponse?.pocContact
                                exEnq?.pocEmail = enquiry?.openEnquiriesResponse?.pocEmail
                                exEnq?.gst = enquiry?.openEnquiriesResponse?.gst

                                realm.copyToRealmOrUpdate(exEnq)
                            }else{
                                nextID = enqObj?._id ?: 0

                                enqObj?.enquiryID = enquiry?.openEnquiriesResponse?.enquiryId
                                enqObj?.enquiryCode = enquiry?.openEnquiriesResponse?.enquiryCode
                                enqObj?.productID = enquiry?.openEnquiriesResponse?.productId
                                enqObj?.productName = enquiry?.openEnquiriesResponse?.productName
                                enqObj?.productCode = enquiry?.openEnquiriesResponse?.productCode
                                enqObj?.enquiryStatusID = enquiry?.openEnquiriesResponse?.enquiryStatusId
                                enqObj?.enquiryStageID = enquiry?.openEnquiriesResponse?.enquiryStageId
                                enqObj?.innerEnquiryStageID = enquiry?.openEnquiriesResponse?.innerEnquiryStageId
                                enqObj?.productStatusID = enquiry?.openEnquiriesResponse?.productStatusId

                                enqObj?.orderCode = enquiry?.openEnquiriesResponse?.orderCode
                                enqObj?.orderCreatedOn = enquiry?.openEnquiriesResponse?.orderCreatedOn
                                enqObj?.expectedDate = enquiry?.openEnquiriesResponse?.excpectedDate
                                enqObj?.startedOn = enquiry?.openEnquiriesResponse?.startedOn
                                enqObj?.lastUpdated = enquiry?.openEnquiriesResponse?.lastUpdated
                                enqObj?.totalAmount = enquiry?.openEnquiriesResponse?.totalAmount

                                enqObj?.isMoqSend = enquiry?.openEnquiriesResponse?.isMoqSend?:0
                                enqObj?.isPiSend = enquiry?.openEnquiriesResponse?.isPiSend?:0
                                enqObj?.changeRequestOn = enquiry?.openEnquiriesResponse?.changeRequestOn
                                enqObj?.isMoqRejected = enquiry?.isMoqRejected?:0
                                enqObj?.isBlue = enquiry?.isBlue

                                enqObj?.productType = enquiry?.openEnquiriesResponse?.productType
                                enqObj?.productCategoryID = enquiry?.openEnquiriesResponse?.productCategoryId
                                enqObj?.warpYarnID = enquiry?.openEnquiriesResponse?.warpYarnId
                                enqObj?.weftYarnID = enquiry?.openEnquiriesResponse?.weftYarnId
                                enqObj?.extraWeftYarnID = enquiry?.openEnquiriesResponse?.extraWeftYarnId
                                enqObj?.productImages = enquiry?.openEnquiriesResponse?.productImages
                                enqObj?.madeWithAnthran = enquiry?.openEnquiriesResponse?.isMoqSend
//                                enqObj?.brandName = enquiry?.openEnquiriesResponse?.companyName //TODO uncomment after enquiry id issue is fixed

                                enqObj?.historyProductID = enquiry?.openEnquiriesResponse?.historyProductId
                                enqObj?.productHistoryName = enquiry?.openEnquiriesResponse?.productHistoryName
                                enqObj?.productHistoryCode = enquiry?.openEnquiriesResponse?.productHistoryCode
                                enqObj?.productCategoryHistoryID = enquiry?.openEnquiriesResponse?.productCategoryHistoryId
                                enqObj?.productHistoryImages = enquiry?.openEnquiriesResponse?.productHistoryImages
                                enqObj?.warpYarnHistoryID = enquiry?.openEnquiriesResponse?.warpYarnHistoryId
                                enqObj?.weftYarnHistoryID = enquiry?.openEnquiriesResponse?.weftYarnHistoryId
                                enqObj?.extraWeftYarnHistoryID = enquiry?.openEnquiriesResponse?.extraWeftYarnHistoryId
                                enqObj?.productStatusHistoryID = enquiry?.openEnquiriesResponse?.productStatusHistoryId
                                enqObj?.madeWithAnthranHistory = enquiry?.openEnquiriesResponse?.madeWittAnthranHistory

                                enqObj?.userId = enquiry?.userId
                                enqObj?.clusterName = enquiry?.clusterName
                                enqObj?.firstName = enquiry?.openEnquiriesResponse?.firstName
                                enqObj?.lastName = enquiry?.openEnquiriesResponse?.lastName
                                enqObj?.brandDesc = enquiry?.openEnquiriesResponse?.description
                                enqObj?.profileImage = enquiry?.openEnquiriesResponse?.profilePic
                                enqObj?.alternateMobile = enquiry?.openEnquiriesResponse?.alternateMobile
//                                enqObj?.companyName = enquiry?.openEnquiriesResponse?.companyName
//                                enqObj?.ProductBrandName = enquiry?.openEnquiriesResponse?.companyName//todo : to be changed

                                enqObj?.logo = enquiry?.openEnquiriesResponse?.logo
                                enqObj?.city = enquiry?.openEnquiriesResponse?.city
                                enqObj?.district = enquiry?.openEnquiriesResponse?.district
                                enqObj?.pincode = enquiry?.openEnquiriesResponse?.pincode
                                enqObj?.line1 = enquiry?.openEnquiriesResponse?.line1
                                enqObj?.line2 = enquiry?.openEnquiriesResponse?.line2
                                enqObj?.street = enquiry?.openEnquiriesResponse?.street
                                enqObj?.state = enquiry?.openEnquiriesResponse?.state
                                enqObj?.country = enquiry?.openEnquiriesResponse?.country
                                enqObj?.email = enquiry?.openEnquiriesResponse?.email
                                enqObj?.mobile = enquiry?.openEnquiriesResponse?.mobile
                                enqObj?.pocFirstName = enquiry?.openEnquiriesResponse?.pocFirstName
                                enqObj?.pocLastName = enquiry?.openEnquiriesResponse?.pocLastName
                                enqObj?.pocContact = enquiry?.openEnquiriesResponse?.pocContact
                                enqObj?.pocEmail = enquiry?.openEnquiriesResponse?.pocEmail
                                enqObj?.gst = enquiry?.openEnquiriesResponse?.gst

                                realm.copyToRealmOrUpdate(enqObj)
                            }
                        }

                    }
                }catch (e:Exception){
                    Log.e("InsertOnEnquiry",e.printStackTrace().toString())
                }
            }
        }

        fun insertCompletedEnquiries(enquiryDetails : EnquiryResponse?){
            val realm = CXRealmManager.getRealmInstance()
            var enquiryList = enquiryDetails?.data
            var enqItr = enquiryList?.iterator()
            realm.executeTransaction{
                try {
                    if(enqItr!=null) {
                        while (enqItr.hasNext()) {
                            var enquiry = enqItr.next()
                            var enqObj = realm.where(CompletedEnquiries::class.java)
                                .equalTo(
                                    CompletedEnquiries.COLUMN_ENQUIRY_ID,
                                    enquiry.openEnquiriesResponse?.enquiryId
                                )
                                .or()
                                .equalTo(CompletedEnquiries.COLUMN_ENQUIRY_CODE,enquiry?.openEnquiriesResponse?.enquiryCode)
                                .limit(1)
                                .findFirst()
                            if (enqObj == null) {
                                var primId = it.where(CompletedEnquiries::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }

                                var exEnq = it.createObject(CompletedEnquiries::class.java, nextID)
                                exEnq?.enquiryID = enquiry?.openEnquiriesResponse?.enquiryId
                                exEnq?.enquiryCode = enquiry?.openEnquiriesResponse?.enquiryCode
                                exEnq?.productID = enquiry?.openEnquiriesResponse?.productId
                                exEnq?.productName = enquiry?.openEnquiriesResponse?.productName
                                exEnq?.productCode = enquiry?.openEnquiriesResponse?.productCode
                                exEnq?.enquiryStatusID = enquiry?.openEnquiriesResponse?.enquiryStatusId
                                exEnq?.enquiryStageID = enquiry?.openEnquiriesResponse?.enquiryStageId
                                exEnq?.innerEnquiryStageID = enquiry?.openEnquiriesResponse?.innerEnquiryStageId
                                exEnq?.productStatusID = enquiry?.openEnquiriesResponse?.productStatusId

                                exEnq?.orderCode = enquiry?.openEnquiriesResponse?.orderCode
                                exEnq?.orderCreatedOn = enquiry?.openEnquiriesResponse?.orderCreatedOn
                                exEnq?.expectedDate = enquiry?.openEnquiriesResponse?.excpectedDate
                                exEnq?.startedOn = enquiry?.openEnquiriesResponse?.startedOn
                                exEnq?.lastUpdated = enquiry?.openEnquiriesResponse?.lastUpdated
                                exEnq?.totalAmount = enquiry?.openEnquiriesResponse?.totalAmount

                                exEnq?.isMoqSend = enquiry?.openEnquiriesResponse?.isMoqSend?:0
                                exEnq?.isPiSend = enquiry?.openEnquiriesResponse?.isPiSend?:0
                                exEnq?.changeRequestOn = enquiry?.openEnquiriesResponse?.changeRequestOn
                                exEnq?.isMoqRejected = enquiry?.isMoqRejected?:0
                                exEnq?.isBlue = enquiry?.isBlue

                                exEnq?.productType = enquiry?.openEnquiriesResponse?.productType
                                exEnq?.productCategoryID = enquiry?.openEnquiriesResponse?.productCategoryId
                                exEnq?.warpYarnID = enquiry?.openEnquiriesResponse?.warpYarnId
                                exEnq?.weftYarnID = enquiry?.openEnquiriesResponse?.weftYarnId
                                exEnq?.extraWeftYarnID = enquiry?.openEnquiriesResponse?.extraWeftYarnId
                                exEnq?.productImages = enquiry?.openEnquiriesResponse?.productImages
                                exEnq?.madeWithAnthran = enquiry?.openEnquiriesResponse?.isMoqSend
//                                exEnq?.brandName = enquiry?.brandName

                                exEnq?.historyProductID = enquiry?.openEnquiriesResponse?.historyProductId
                                exEnq?.productHistoryName = enquiry?.openEnquiriesResponse?.productHistoryName
                                exEnq?.productHistoryCode = enquiry?.openEnquiriesResponse?.productHistoryCode
                                exEnq?.productCategoryHistoryID = enquiry?.openEnquiriesResponse?.productCategoryHistoryId
                                exEnq?.productHistoryImages = enquiry?.openEnquiriesResponse?.productHistoryImages
                                exEnq?.warpYarnHistoryID = enquiry?.openEnquiriesResponse?.warpYarnHistoryId
                                exEnq?.weftYarnHistoryID = enquiry?.openEnquiriesResponse?.weftYarnHistoryId
                                exEnq?.extraWeftYarnHistoryID = enquiry?.openEnquiriesResponse?.extraWeftYarnHistoryId
                                exEnq?.productStatusHistoryID = enquiry?.openEnquiriesResponse?.productStatusHistoryId
                                exEnq?.madeWithAnthranHistory = enquiry?.openEnquiriesResponse?.madeWittAnthranHistory

                                exEnq?.userId = enquiry?.userId
                                exEnq?.clusterName = enquiry?.clusterName
                                exEnq?.firstName = enquiry?.openEnquiriesResponse?.firstName
                                exEnq?.lastName = enquiry?.openEnquiriesResponse?.lastName
                                exEnq?.brandDesc = enquiry?.openEnquiriesResponse?.description
                                exEnq?.profileImage = enquiry?.openEnquiriesResponse?.profilePic
                                exEnq?.alternateMobile = enquiry?.openEnquiriesResponse?.alternateMobile
//                                exEnq?.companyName = enquiry?.openEnquiriesResponse?.companyName
                                exEnq?.ProductBrandName = enquiry?.brandName //todo : to be changed
                                exEnq?.logo = enquiry?.openEnquiriesResponse?.logo
                                exEnq?.city = enquiry?.openEnquiriesResponse?.city
                                exEnq?.district = enquiry?.openEnquiriesResponse?.district
                                exEnq?.pincode = enquiry?.openEnquiriesResponse?.pincode
                                exEnq?.line1 = enquiry?.openEnquiriesResponse?.line1
                                exEnq?.line2 = enquiry?.openEnquiriesResponse?.line2
                                exEnq?.street = enquiry?.openEnquiriesResponse?.street
                                exEnq?.state = enquiry?.openEnquiriesResponse?.state
                                exEnq?.country = enquiry?.openEnquiriesResponse?.country
                                exEnq?.email = enquiry?.openEnquiriesResponse?.email
                                exEnq?.mobile = enquiry?.openEnquiriesResponse?.mobile
                                exEnq?.pocFirstName = enquiry?.openEnquiriesResponse?.pocFirstName
                                exEnq?.pocLastName = enquiry?.openEnquiriesResponse?.pocLastName
                                exEnq?.pocContact = enquiry?.openEnquiriesResponse?.pocContact
                                exEnq?.pocEmail = enquiry?.openEnquiriesResponse?.pocEmail
                                exEnq?.gst = enquiry?.openEnquiriesResponse?.gst

                                realm.copyToRealmOrUpdate(exEnq)
                            }else{
                                nextID = enqObj?._id ?: 0

                                enqObj?.enquiryID = enquiry?.openEnquiriesResponse?.enquiryId
                                enqObj?.enquiryCode = enquiry?.openEnquiriesResponse?.enquiryCode
                                enqObj?.productID = enquiry?.openEnquiriesResponse?.productId
                                enqObj?.productName = enquiry?.openEnquiriesResponse?.productName
                                enqObj?.productCode = enquiry?.openEnquiriesResponse?.productCode
                                enqObj?.enquiryStatusID = enquiry?.openEnquiriesResponse?.enquiryStatusId
                                enqObj?.enquiryStageID = enquiry?.openEnquiriesResponse?.enquiryStageId
                                enqObj?.innerEnquiryStageID = enquiry?.openEnquiriesResponse?.innerEnquiryStageId
                                enqObj?.productStatusID = enquiry?.openEnquiriesResponse?.productStatusId

                                enqObj?.orderCode = enquiry?.openEnquiriesResponse?.orderCode
                                enqObj?.orderCreatedOn = enquiry?.openEnquiriesResponse?.orderCreatedOn
                                enqObj?.expectedDate = enquiry?.openEnquiriesResponse?.excpectedDate
                                enqObj?.startedOn = enquiry?.openEnquiriesResponse?.startedOn
                                enqObj?.lastUpdated = enquiry?.openEnquiriesResponse?.lastUpdated
                                enqObj?.totalAmount = enquiry?.openEnquiriesResponse?.totalAmount

                                enqObj?.isMoqSend = enquiry?.openEnquiriesResponse?.isMoqSend?:0
                                enqObj?.isPiSend = enquiry?.openEnquiriesResponse?.isPiSend?:0
                                enqObj?.changeRequestOn = enquiry?.openEnquiriesResponse?.changeRequestOn
                                enqObj?.isMoqRejected = enquiry?.isMoqRejected?:0
                                enqObj?.isBlue = enquiry?.isBlue

                                enqObj?.productType = enquiry?.openEnquiriesResponse?.productType
                                enqObj?.productCategoryID = enquiry?.openEnquiriesResponse?.productCategoryId
                                enqObj?.warpYarnID = enquiry?.openEnquiriesResponse?.warpYarnId
                                enqObj?.weftYarnID = enquiry?.openEnquiriesResponse?.weftYarnId
                                enqObj?.extraWeftYarnID = enquiry?.openEnquiriesResponse?.extraWeftYarnId
                                enqObj?.productImages = enquiry?.openEnquiriesResponse?.productImages
                                enqObj?.madeWithAnthran = enquiry?.openEnquiriesResponse?.isMoqSend
//                                enqObj?.brandName = enquiry?.openEnquiriesResponse?.companyName //TODO uncomment after enquiry id issue is fixed

                                enqObj?.historyProductID = enquiry?.openEnquiriesResponse?.historyProductId
                                enqObj?.productHistoryName = enquiry?.openEnquiriesResponse?.productHistoryName
                                enqObj?.productHistoryCode = enquiry?.openEnquiriesResponse?.productHistoryCode
                                enqObj?.productCategoryHistoryID = enquiry?.openEnquiriesResponse?.productCategoryHistoryId
                                enqObj?.productHistoryImages = enquiry?.openEnquiriesResponse?.productHistoryImages
                                enqObj?.warpYarnHistoryID = enquiry?.openEnquiriesResponse?.warpYarnHistoryId
                                enqObj?.weftYarnHistoryID = enquiry?.openEnquiriesResponse?.weftYarnHistoryId
                                enqObj?.extraWeftYarnHistoryID = enquiry?.openEnquiriesResponse?.extraWeftYarnHistoryId
                                enqObj?.productStatusHistoryID = enquiry?.openEnquiriesResponse?.productStatusHistoryId
                                enqObj?.madeWithAnthranHistory = enquiry?.openEnquiriesResponse?.madeWittAnthranHistory

                                enqObj?.userId = enquiry?.userId
                                enqObj?.clusterName = enquiry?.clusterName
                                enqObj?.firstName = enquiry?.openEnquiriesResponse?.firstName
                                enqObj?.lastName = enquiry?.openEnquiriesResponse?.lastName
                                enqObj?.brandDesc = enquiry?.openEnquiriesResponse?.description
                                enqObj?.profileImage = enquiry?.openEnquiriesResponse?.profilePic
                                enqObj?.alternateMobile = enquiry?.openEnquiriesResponse?.alternateMobile
//                                enqObj?.companyName = enquiry?.openEnquiriesResponse?.companyName
//                                enqObj?.ProductBrandName = enquiry?.openEnquiriesResponse?.companyName//todo : to be changed

                                enqObj?.logo = enquiry?.openEnquiriesResponse?.logo
                                enqObj?.city = enquiry?.openEnquiriesResponse?.city
                                enqObj?.district = enquiry?.openEnquiriesResponse?.district
                                enqObj?.pincode = enquiry?.openEnquiriesResponse?.pincode
                                enqObj?.line1 = enquiry?.openEnquiriesResponse?.line1
                                enqObj?.line2 = enquiry?.openEnquiriesResponse?.line2
                                enqObj?.street = enquiry?.openEnquiriesResponse?.street
                                enqObj?.state = enquiry?.openEnquiriesResponse?.state
                                enqObj?.country = enquiry?.openEnquiriesResponse?.country
                                enqObj?.email = enquiry?.openEnquiriesResponse?.email
                                enqObj?.mobile = enquiry?.openEnquiriesResponse?.mobile
                                enqObj?.pocFirstName = enquiry?.openEnquiriesResponse?.pocFirstName
                                enqObj?.pocLastName = enquiry?.openEnquiriesResponse?.pocLastName
                                enqObj?.pocContact = enquiry?.openEnquiriesResponse?.pocContact
                                enqObj?.pocEmail = enquiry?.openEnquiriesResponse?.pocEmail
                                enqObj?.gst = enquiry?.openEnquiriesResponse?.gst

                                realm.copyToRealmOrUpdate(enqObj)
                            }
                        }

                    }
                }catch (e:Exception){
                    Log.e("InsertCompEnquiry",e.printStackTrace().toString())
                }
            }
        }

        fun insertEnqArtisanProductCategory(details : EnquiryResponse?){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm?.executeTransaction {
                    var detItr = details?.data?.iterator()
                    if(detItr!=null){
                        while (detItr.hasNext()){
                            var details = detItr.next()
                            var prodIterator = details?.productCategories?.iterator()
                            if (prodIterator != null) {
                                while (prodIterator.hasNext()) {
                                    var prod = prodIterator.next()
                                    var prodObj = realm.where(ArtisanProductCategory::class.java)
//                                        .equalTo("id", prod.id) //TODO : to be fixed from backend n then changed
                                        .equalTo("userid",prod.userId)
                                        .and()
                                        .equalTo("productCategoryid",prod.productCategoryId)
                                        .limit(1)
                                        .findFirst()

                                    if (prodObj == null) {
                                        var primId = it.where(ArtisanProductCategory::class.java).max("_id")
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var exprod = it.createObject(
                                            ArtisanProductCategory::class.java,
                                            nextID
                                        )
                                        exprod.id = prod.id
                                        exprod.userid = prod.userId
                                        exprod.productCategoryid = prod.productCategoryId

                                        realm.copyToRealmOrUpdate(exprod)
                                    } else {
                                        nextID = prodObj._id ?: 0
                                        prodObj.id = prod.id
                                        prodObj.userid = prod.userId
                                        prodObj.productCategoryid = prod.productCategoryId

                                        realm.copyToRealmOrUpdate(prodObj)
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProdCatEnq","${e.printStackTrace()}")
            }
        }

        fun insertEnqPaymentDetails(details : EnquiryResponse?){
            val realm = CXRealmManager.getRealmInstance()
            var detailsItr = details?.data?.iterator()
                realm.executeTransaction {
                    try {
                    if (detailsItr != null) {
                        while (detailsItr?.hasNext()) {
                            var details = detailsItr.next()
                            var payItr = details?.paymentAccountDetails?.iterator()
                            if(payItr!=null){
                                while(payItr.hasNext()){
                                    var pay = payItr.next()
                                    var payObj = realm.where(EnquiryPaymentDetails::class.java)
                                        .equalTo(EnquiryPaymentDetails.COLUMN_ID, pay.id)
                                        .limit(1)
                                        .findFirst()

                                    if (payObj == null) {
                                        var primId = it.where(EnquiryPaymentDetails::class.java).max(EnquiryPaymentDetails.COLUMN__ID)
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var expay = it.createObject(
                                            EnquiryPaymentDetails::class.java,
                                            nextID
                                        )

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
                    }
                    }catch (e: Exception) {
                        Log.e("PaymentEnq","${e.printStackTrace()}")
                }

            }
        }

        fun insertEnquiryProduct(details: EnquiryProductResponse?,isCustom : Boolean?){
            nextID = 0L
            Log.e("EnquiryProduct","details ${details?.data?.artistName}")
            val realm = CXRealmManager.getRealmInstance()
            val prod = details?.data
            try {
                realm.executeTransaction {
                    var productObj = realm.where(EnquiryProductDetails::class.java)
                        .equalTo(EnquiryProductDetails.COLUMN_PRODUCT_ID,prod?.id)
                        .limit(1)
                        .findFirst()
                    if (productObj == null){
                        var primId = it.where(EnquiryProductDetails::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var exprod = it.createObject(EnquiryProductDetails::class.java,nextID )
                        Log.e("EnquiryProduct","1111111 ${prod?.artitionId}")
                        exprod.isCustom = isCustom
                        exprod.artisanId = prod?.artitionId
                        exprod.artisanName = prod?.artistName
                        exprod.clusterId = prod?.clusterId
                        exprod.clusterName = prod?.clusterName
                        Log.e("EnquiryProduct","22222222 ${prod?.id}")
//                        exprod.brandName = catalogueProduct?.brand
//                        productId= catalogueProduct?.id
                        exprod.productId = prod?.id
                        exprod.productCode = prod?.code
                        exprod.productTag = prod?.tag

                        exprod.productCategoryId = prod?.productCategory?.id
                        exprod.productCategoryName = prod?.productCategory?.productDesc
                        exprod.productCategoryCode = prod?.productCategory?.code
                        Log.e("EnquiryProduct","333333333333 ${prod?.id}")
                        exprod.productTypeId = prod?.productType?.id
                        exprod.productTypeDesc = prod?.productType?.productDesc
                        exprod.inProductCategory = prod?.productType?.productCategoryId
                        Log.e("EnquiryProduct","444444444444 ${prod?.id}")
                        exprod.warpYarnId = prod?.warpYarn?.id
                        exprod.warpYarnDesc = prod?.warpYarn?.yarnDesc
                        exprod.warpYarnCount = prod?.warpYarnCount

                        Log.e("EnquiryProduct","55555555 ${prod?.id}")
                        exprod.warpDyeId = prod?.warpYarn?.id
                        exprod.warpDyeId = prod?.warpDye?.id
                        exprod.warpDyeDesc = prod?.warpDye?.dyeDesc
                        Log.e("EnquiryProduct","666666666 ${prod?.id}")
                        exprod.weftYarnId = prod?.weftYarn?.id
                        exprod.weftYarnDesc = prod?.weftYarn?.yarnDesc
                        exprod.weftYarnCount = prod?.weftYarnCount
//                                exprod.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.weftDyeId = prod?.weftDye?.id
                        exprod.weftDyeDesc = prod?.weftDye?.dyeDesc
                        Log.e("EnquiryProduct","77777777 ${prod?.id}")
                        exprod.extraWeftYarnId = prod?.extraWeftYarn?.id
                        exprod.extraWeftYarnDesc = prod?.extraWeftYarn?.yarnDesc
                        exprod.extraWeftYarnCount = prod?.extraWeftYarnCount
//                                exprod.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.extraWeftDyeId = prod?.extraWeftDye?.id
                        exprod.extraWeftDyeDesc = prod?.extraWeftDye?.dyeDesc
                        Log.e("EnquiryProduct","88888888888 ${prod?.id}")
                        exprod.productLength = prod?.length
                        exprod.productWidth = prod?.width

                        exprod.reedCountId = prod?.reedCount?.id
                        exprod.reedCount = prod?.reedCount?.count
                        Log.e("EnquiryProduct","99999999999 ${prod?.id}")
                        exprod.productStatusId = prod?.productStatusId

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        exprod.gsm = prod?.gsm
                        exprod.weight = prod?.weight
                        exprod.product_spe = prod?.product_spe
                        Log.e("EnquiryProduct","aaaaaaaaaaa ${prod?.id}")
//                        exprod.createdOn = catalogueProduct?.createdOn
//                        exprod.modifiedOn = catalogueProduct?.modifiedOn
//                        exprod.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        exprod.isDeleted = prod?.isDeleted
                        Log.e("EnquiryProduct","bbbbbbbbbbb ${prod?.id}")
                        realm.copyToRealmOrUpdate(exprod)
                    }
                    else{
                        nextID = productObj._id ?: 0
                        productObj.isCustom = isCustom
                        productObj.artisanId = prod?.artitionId
                        productObj.artisanName = prod?.artistName
                        productObj.clusterId = prod?.clusterId
                        productObj.clusterName = prod?.clusterName
//                        productObj.brandName = catalogueProduct?.brand

                        productObj.productId = prod?.id
                        productObj.productCode = prod?.code
                        productObj.productTag = prod?.tag

                        productObj.productCategoryId = prod?.productCategory?.id
                        productObj.productCategoryName = prod?.productCategory?.productDesc
                        productObj.productCategoryCode = prod?.productCategory?.code

                        productObj.productTypeId = prod?.productType?.id
                        productObj.productTypeDesc = prod?.productType?.productDesc
                        productObj.inProductCategory = prod?.productType?.productCategoryId

                        productObj.warpYarnId = prod?.warpYarn?.id
                        productObj.warpYarnDesc = prod?.warpYarn?.yarnDesc
                        productObj.warpYarnCount = prod?.warpYarnCount
//                                productObj.warpYarnTypeId = catalogueProduct?.warpYarn?.yarnType?.id TODO : to be used later if required
                        productObj.warpDyeId = prod?.warpYarn?.id
                        productObj.warpDyeId = prod?.warpDye?.id
                        productObj.warpDyeDesc = prod?.warpDye?.dyeDesc

                        productObj.weftYarnId = prod?.weftYarn?.id
                        productObj.weftYarnDesc = prod?.weftYarn?.yarnDesc
                        productObj.weftYarnCount = prod?.weftYarnCount
//                                productObj.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.weftDyeId = prod?.weftDye?.id
                        productObj.weftDyeDesc = prod?.weftDye?.dyeDesc

                        productObj.extraWeftYarnId = prod?.extraWeftYarn?.id
                        productObj.extraWeftYarnDesc = prod?.extraWeftYarn?.yarnDesc
                        productObj.extraWeftYarnCount = prod?.extraWeftYarnCount
//                                productObj.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.extraWeftDyeId = prod?.extraWeftDye?.id
                        productObj.extraWeftDyeDesc = prod?.extraWeftDye?.dyeDesc

                        productObj.productLength = prod?.length
                        productObj.productWidth = prod?.width

                        productObj.reedCountId = prod?.reedCount?.id
                        productObj.reedCount = prod?.reedCount?.count

                        productObj.productStatusId = prod?.productStatusId

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        productObj.gsm = prod?.gsm
                        productObj.weight = prod?.weight
                        productObj.product_spe = prod?.product_spe

//                        productObj.createdOn = catalogueProduct?.createdOn
//                        productObj.modifiedOn = catalogueProduct?.modifiedOn
//                        productObj.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        productObj.isDeleted = prod?.isDeleted

                        realm.copyToRealmOrUpdate(productObj)
                    }

                    var imageList = prod?.productImages
                    var imageItr = imageList?.iterator()
                    if(imageItr!=null){
                        while (imageItr.hasNext()){
                            var image = imageItr.next()

                            var imageObj = realm.where(ProductImages::class.java)
                                .equalTo("imageId",image.id)
                                .limit(1)
                                .findFirst()
                            if(imageObj == null){
                                var primId = it.where(ProductImages::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var eximg = it.createObject(
                                    ProductImages::class.java,
                                    nextID
                                )
                                eximg.productId = prod?.id
                                eximg.imageId = image?.id
                                eximg.imageName = image?.lable
                                realm.copyToRealmOrUpdate(eximg)
                            }else{
                                nextID = imageObj._id ?: 0
                                imageObj.productId = prod?.id
                                imageObj.imageId = image?.id
                                imageObj.imageName = image?.lable
                                realm.copyToRealmOrUpdate(imageObj)
                            }

                        }
                    }

                    var relatedProductList = prod?.relProduct
                    var prodItr = relatedProductList?.iterator()
                    if(prodItr!=null){
                        while (prodItr.hasNext()){
                            var relProduct = prodItr.next()

                            var relPoprductObj = realm.where(RelatedProducts::class.java)
                                .equalTo("relatedProductId",relProduct.id)
                                .limit(1)
                                .findFirst()
                            if(relPoprductObj == null){
                                var primId = it.where(RelatedProducts::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exRelProd= it.createObject(
                                    RelatedProducts::class.java,
                                    nextID
                                )
                                exRelProd?.inProductCategoryId = prod?.productCategory?.id
                                exRelProd?.relatedToProductId = prod?.id
                                exRelProd?.relatedProductId = relProduct?.id
                                exRelProd?.productTypeId = relProduct?.productType.id
                                exRelProd?.productName = relProduct?.productType.productDesc
                                exRelProd?.productLength = relProduct?.length
                                exRelProd?.productWidth = relProduct?.width
                                exRelProd?.productWeight = relProduct?.weight

                                realm.copyToRealmOrUpdate(exRelProd)
                            }else{
                                relPoprductObj.inProductCategoryId = prod?.productCategory?.id
                                relPoprductObj.relatedToProductId = prod?.id
                                relPoprductObj.relatedProductId = relProduct?.id
                                relPoprductObj.productTypeId = relProduct?.productType?.id
                                relPoprductObj.productName = relProduct?.productType?.productDesc
                                relPoprductObj.productLength = relProduct?.length
                                relPoprductObj.productWidth = relProduct?.width
                                relPoprductObj.productWeight = relProduct?.weight

                                realm.copyToRealmOrUpdate(relPoprductObj)
                            }
                        }
                    }

                    var weaveTypeList = prod?.productWeaves
                    var weaveTypeItr = weaveTypeList?.iterator()
                    if(weaveTypeItr!=null){
                        while (weaveTypeItr.hasNext()){
                            var weaveType = weaveTypeItr.next()

                            var weaveTypeObj = realm.where(WeaveTypes::class.java)
                                .equalTo("productId",weaveType.productId)
                                .and()
                                .equalTo("weaveId",weaveType.weaveId)
                                .findFirst()
                            if(weaveTypeObj == null){
                                var primId = it.where(WeaveTypes::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exWeaveType = it.createObject(
                                    WeaveTypes::class.java,
                                    nextID
                                )
                                exWeaveType?.productId = weaveType?.productId
                                exWeaveType?.productWeaveId = weaveType?.id
                                exWeaveType?.weaveId = weaveType?.weaveId
                                realm.copyToRealmOrUpdate(exWeaveType)
                            }else{
                                nextID = weaveTypeObj._id ?: 0
                                weaveTypeObj.productId = weaveType?.productId
                                weaveTypeObj.productWeaveId = weaveType?.id
                                weaveTypeObj.weaveId = weaveType?.weaveId
                                realm.copyToRealmOrUpdate(weaveTypeObj)
                            }

                        }
                    }

                    var careList = prod?.productCares
                    var careItr = careList?.iterator()
                    if(careItr!=null){
                        while (careItr.hasNext()){
                            var care = careItr.next()

                            var careObj = realm.where(ProductCares::class.java)
                                .equalTo("productId",care.productId)
                                .and()
                                .equalTo("productCareId",care.productCareId)
                                .limit(1)
                                .findFirst()
                            if(careObj == null){
                                var primId = it.where(ProductCares::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exCare = it.createObject(
                                    ProductCares::class.java,
                                    nextID
                                )
                                exCare?.productId = care?.productId
                                exCare?.careId = care?.id
                                exCare?.productCareId = care?.productCareId
                                realm.copyToRealmOrUpdate(exCare)
                            }else{
                                nextID = careObj?._id ?: 0
                                careObj?.productId = care?.productId
                                careObj?.careId = care?.id
                                careObj?.productCareId = care?.productCareId
                                realm.copyToRealmOrUpdate(careObj)
                            }

                        }
                    }
                }

            }catch (e:Exception){
                Log.e("EnquiryProduct","$e")
            }
        }

        fun getEnqProduct(productId : Long?,isCustom : Boolean?) : EnquiryProductDetails? {
            val realm = CXRealmManager.getRealmInstance()
            var prod : EnquiryProductDetails ?= null
            realm?.executeTransaction {
                try {
                    prod = realm.where(EnquiryProductDetails::class.java)
                        .equalTo(EnquiryProductDetails.COLUMN_IS_CUSTOM,isCustom)
                        .and()
                        .equalTo(EnquiryProductDetails.COLUMN_PRODUCT_ID,productId)
                        .findFirst()
                }catch (e:Exception){
                    Log.e("EnqProduct","${e.printStackTrace()}")
                }
            }
            return prod
        }


        fun getProdCatEnq(artisanId : Long?) : RealmResults<ArtisanProductCategory>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ArtisanProductCategory::class.java)
                .equalTo("userid",artisanId)
                .distinct("productCategoryid")
                .findAll()
        }

        fun getEnqPaymentDetails(userid: String,accountid : Long): EnquiryPaymentDetails? {
            val realm = CXRealmManager.getRealmInstance()
            var payment : EnquiryPaymentDetails?= null
            try {
                realm.executeTransaction {
                    payment = realm.where(EnquiryPaymentDetails::class.java)
                        .equalTo("userid", userid.toLong())
                        .and()
                        .equalTo("accountid", accountid)
                        .limit(1)
                        .findFirst()
                }
            } catch (e: Exception) {
               Log.e("Payment Enquiry","${e.printStackTrace()}")
            }
            return payment
        }


        fun getAllOngoingEnquiries(): RealmResults<OngoingEnquiries>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(OngoingEnquiries::class.java).sort(OngoingEnquiries.COLUMN_LAST_UPDATED,Sort.DESCENDING).findAll()
        }

        fun getAllCompletedEnquiries(): RealmResults<CompletedEnquiries>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(CompletedEnquiries::class.java).sort(CompletedEnquiries.COLUMN_LAST_UPDATED,Sort.DESCENDING).findAll()
        }

        fun getSingleOnGoEnquiryDetails(enquiryId : Long?): OngoingEnquiries? {
            var realm = CXRealmManager.getRealmInstance()
            var enquiry : OngoingEnquiries?= null
            realm.executeTransaction {
                try{
                    enquiry = realm.where(OngoingEnquiries::class.java)
                        .equalTo(OngoingEnquiries.COLUMN_ENQUIRY_ID,enquiryId)
//                        .or()
//                        .equalTo(OngoingEnquiries.COLUMN_ENQUIRY_CODE,enqCode)
                        .limit(1)
                        .findFirst()

                }catch (e:Exception){
                    Log.e("EnquiryDetails","Exception : "+e.printStackTrace())
                }
            }
            return enquiry
        }

        fun getSingleCompEnquiryDetails(enquiryId : Long?): CompletedEnquiries? {
            var realm = CXRealmManager.getRealmInstance()
            var enquiry : CompletedEnquiries?= null
            realm.executeTransaction {
                try{
                    enquiry = realm.where(CompletedEnquiries::class.java)
                        .equalTo(CompletedEnquiries.COLUMN_ENQUIRY_ID,enquiryId)
//                        .or()
//                        .equalTo(OngoingEnquiries.COLUMN_ENQUIRY_CODE,enqCode)
                        .limit(1)
                        .findFirst()

                }catch (e:Exception){
                    Log.e("EnquiryDetails","Exception : "+e.printStackTrace())
                }
            }
            return enquiry
        }

        fun deleteEnquiry(enquiryId: Long?){
            var realm = CXRealmManager.getRealmInstance()
            var enquiry : OngoingEnquiries?= null
            realm.executeTransaction {
                try{
                    enquiry = realm.where(OngoingEnquiries::class.java)
                        .equalTo(OngoingEnquiries.COLUMN_ENQUIRY_ID,enquiryId)
                        .limit(1)
                        .findFirst()

                    enquiry?.deleteFromRealm()

                }catch (e:Exception){
                    Log.e("EnquiryDetails","Exception : "+e.printStackTrace())
                }
            }
        }

    }
}