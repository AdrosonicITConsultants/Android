package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.Enquiries
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
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
    }
}