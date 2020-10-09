package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.orders.OrderResponse
import io.realm.RealmResults
import io.realm.Sort
import java.lang.Exception

class OrdersPredicates {
    companion object {
        private val TAG="OrdersPredicates"
        private var nextID : Long? = 0

        fun insertOngoingOrders(orderDetails : OrderResponse?,isCompleted:Long){
            val realm = CXRealmManager.getRealmInstance()
            var orderList = orderDetails?.data
            var idList=ArrayList<Long>()
            var iterator = orderList?.iterator()
            realm.executeTransaction{
                try {
                    if(iterator!=null) {
                        while (iterator.hasNext()) {

                            Log.e("OrderDetails","isCompleted: "+isCompleted)
                            var order = iterator.next()
                            idList.add(order?.openEnquiriesResponse?.enquiryId)
                            var orderObj = realm.where(Orders::class.java)
                                .equalTo(Orders.COLUMN_ORDER_CODE, order.openEnquiriesResponse?.orderCode )
                                .limit(1)
                                .findFirst()
                            if (orderObj == null) {
                                var primId = it.where(Orders::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }

                                var exEnq = it.createObject(Orders::class.java, nextID)
                                exEnq?.comment = order?.openEnquiriesResponse?.comment
                                exEnq?.orderCreatedOn = order?.openEnquiriesResponse?.orderCreatedOn
                                exEnq?.productId = order?.openEnquiriesResponse?.productId
                                exEnq?.companyName = order?.openEnquiriesResponse?.companyName
                                exEnq?.productCategoryId = order?.openEnquiriesResponse?.productCategoryId
                                exEnq?.mobile = order?.openEnquiriesResponse?.mobile
                                exEnq?.profilePic = order?.openEnquiriesResponse?.profilePic
                                exEnq?.firstName = order?.openEnquiriesResponse?.firstName
                                exEnq?.lastName = order?.openEnquiriesResponse?.lastName
                                exEnq?.alternateMobile = order?.openEnquiriesResponse?.alternateMobile
                                exEnq?.totalAmount = order?.openEnquiriesResponse?.totalAmount
                                exEnq?.orderCode = order?.openEnquiriesResponse?.orderCode
                                exEnq?.description = order?.openEnquiriesResponse?.description//todo
                                exEnq?.logo = order?.openEnquiriesResponse?.logo
                                exEnq?.warpYarnId = order?.openEnquiriesResponse?.warpYarnId
                                exEnq?.weftYarnId = order?.openEnquiriesResponse?.weftYarnId
                                exEnq?.extraWeftYarnId = order?.openEnquiriesResponse?.extraWeftYarnId
                                exEnq?.email = order?.openEnquiriesResponse?.email
                                exEnq?.enquiryId = order?.openEnquiriesResponse?.enquiryId
                                exEnq?.line1 = order?.openEnquiriesResponse?.line1
                                exEnq?.line2 = order?.openEnquiriesResponse?.line2
                                exEnq?.street = order?.openEnquiriesResponse?.street
                                exEnq?.city = order?.openEnquiriesResponse?.city
                                exEnq?.pincode = order?.openEnquiriesResponse?.pincode
                                exEnq?.enquiryCode = order?.openEnquiriesResponse?.enquiryCode
                                exEnq?.productStatusId = order?.openEnquiriesResponse?.productStatusId?:0
                                exEnq?.productType = order?.openEnquiriesResponse?.productType?:""
                                exEnq?.district = order?.openEnquiriesResponse?.district
                                exEnq?.productName = order?.openEnquiriesResponse?.productName?:""
                                exEnq?.productCode = order?.openEnquiriesResponse?.productCode
                                exEnq?.productImages = order?.openEnquiriesResponse?.productImages
                                exEnq?.orderReceiveDate = order?.openEnquiriesResponse?.orderReceiveDate
                                exEnq?.excpectedDate = order?.openEnquiriesResponse?.excpectedDate
                                exEnq?.changeRequestModifiedOn = order?.openEnquiriesResponse?.changeRequestModifiedOn
                                exEnq?.innerEnquiryStageId = order?.openEnquiriesResponse?.innerEnquiryStageId
                                exEnq?.enquiryStageId = order?.openEnquiriesResponse?.enquiryStageId
                                exEnq?.startedOn = order?.openEnquiriesResponse?.startedOn
                                exEnq?.changeRequestStatus = order?.openEnquiriesResponse?.changeRequestStatus?:0L
                                exEnq?.pocFirstName = order?.openEnquiriesResponse?.pocFirstName
                                exEnq?.productCategoryHistoryId = order?.openEnquiriesResponse?.productCategoryHistoryId
                                exEnq?.warpYarnHistoryId = order?.openEnquiriesResponse?.warpYarnHistoryId
                                exEnq?.weftYarnHistoryId = order?.openEnquiriesResponse?.weftYarnHistoryId
                                exEnq?.extraWeftYarnHistoryId = order?.openEnquiriesResponse?.extraWeftYarnHistoryId
                                exEnq?.productStatusHistoryId = order?.openEnquiriesResponse?.productStatusHistoryId
                                exEnq?.madeWittAnthranHistory = order?.openEnquiriesResponse?.madeWittAnthranHistory
                                exEnq?.buyerRatingDone = order?.openEnquiriesResponse?.buyerRatingDone
                                exEnq?.deliveryChallanUploaded = order?.openEnquiriesResponse?.deliveryChallanUploaded
                                exEnq?.deliveryChallanLabel = order?.openEnquiriesResponse?.deliveryChallanLabel
                                exEnq?.historyProductId = order?.openEnquiriesResponse?.historyProductId
                                exEnq?.madeWittAnthran = order?.openEnquiriesResponse?.madeWittAnthran
                                exEnq?.productHistoryImages = order?.openEnquiriesResponse?.productHistoryImages
                                exEnq?.enquiryStatusId= order?.openEnquiriesResponse?.enquiryStatusId
                                exEnq?.pocLastName = order?.openEnquiriesResponse?.pocLastName
                                exEnq?.pocEmail = order?.openEnquiriesResponse?.pocEmail
                                exEnq?.pocContact = order?.openEnquiriesResponse?.pocContact
                                exEnq?.gst = order?.openEnquiriesResponse?.gst
                                exEnq?.productHistoryCode = order?.openEnquiriesResponse?.productHistoryCode
                                exEnq?.productHistoryName = order?.openEnquiriesResponse?.productHistoryName
                                exEnq?.lastUpdated = order?.openEnquiriesResponse?.lastUpdated
                                exEnq?.state = order?.openEnquiriesResponse?.state
                                exEnq?.country = order?.openEnquiriesResponse?.country
                                exEnq?.brandName = order?.brandName //todo : to be changed
                                exEnq?.isMoqSend = order?.openEnquiriesResponse?.isMoqSend?:0
                                exEnq?.isPiSend = order?.openEnquiriesResponse?.isPiSend?:0
                                exEnq?.isMoqRejected = order?.isMoqRejected?:0
                                exEnq?.changeRequestOn = order?.openEnquiriesResponse?.changeRequestOn
                                exEnq?.isBlue = order?.isBlue?:0
                                exEnq?.isOrderFromCompleted=isCompleted
                                realm.copyToRealmOrUpdate(exEnq)
                            }else{
                                nextID = orderObj?._id ?: 0
                                Log.e("OrderDetails","1111111111111")
                                orderObj?.comment = order?.openEnquiriesResponse?.comment
                                orderObj?.orderCreatedOn = order?.openEnquiriesResponse?.orderCreatedOn
                                orderObj?.productId = order?.openEnquiriesResponse?.productId
                                orderObj?.companyName = order?.openEnquiriesResponse?.companyName
                                orderObj?.productCategoryId = order?.openEnquiriesResponse?.productCategoryId
                                orderObj?.mobile = order?.openEnquiriesResponse?.mobile
                                orderObj?.profilePic = order?.openEnquiriesResponse?.profilePic
                                orderObj?.firstName = order?.openEnquiriesResponse?.firstName
                                orderObj?.lastName = order?.openEnquiriesResponse?.lastName
                                orderObj?.alternateMobile = order?.openEnquiriesResponse?.alternateMobile
                                orderObj?.totalAmount = order?.openEnquiriesResponse?.totalAmount
                                orderObj?.orderCode = order?.openEnquiriesResponse?.orderCode
                                orderObj?.description = order?.openEnquiriesResponse?.description//todo
                                orderObj?.logo = order?.openEnquiriesResponse?.logo
                                orderObj?.warpYarnId = order?.openEnquiriesResponse?.warpYarnId
                                orderObj?.weftYarnId = order?.openEnquiriesResponse?.weftYarnId
                                orderObj?.extraWeftYarnId = order?.openEnquiriesResponse?.extraWeftYarnId
                                orderObj?.email = order?.openEnquiriesResponse?.email
                                Log.e("OrderDetails","enquiryId "+order?.openEnquiriesResponse?.enquiryId)
//                                orderObj?.enquiryId = order?.openEnquiriesResponse?.enquiryId
                                orderObj?.line1 = order?.openEnquiriesResponse?.line1
                                orderObj?.line2 = order?.openEnquiriesResponse?.line2
                                orderObj?.street = order?.openEnquiriesResponse?.street
                                orderObj?.city = order?.openEnquiriesResponse?.city
                                orderObj?.pincode = order?.openEnquiriesResponse?.pincode
                                orderObj?.enquiryCode = order?.openEnquiriesResponse?.enquiryCode
                                orderObj?.productStatusId = order?.openEnquiriesResponse?.productStatusId?:0
                                orderObj?.productType = order?.openEnquiriesResponse?.productType?:""
                                orderObj?.district = order?.openEnquiriesResponse?.district
                                orderObj?.productName = order?.openEnquiriesResponse?.productName?:""
                                Log.e("OrderDetails","33333333333333333")
                                orderObj?.productCode = order?.openEnquiriesResponse?.productCode
                                orderObj?.productImages = order?.openEnquiriesResponse?.productImages
                                orderObj?.orderReceiveDate = order?.openEnquiriesResponse?.orderReceiveDate
                                orderObj?.excpectedDate = order?.openEnquiriesResponse?.excpectedDate
                                orderObj?.changeRequestModifiedOn = order?.openEnquiriesResponse?.changeRequestModifiedOn
                                orderObj?.innerEnquiryStageId = order?.openEnquiriesResponse?.innerEnquiryStageId
                                orderObj?.enquiryStageId = order?.openEnquiriesResponse?.enquiryStageId
                                orderObj?.startedOn = order?.openEnquiriesResponse?.startedOn
                                orderObj?.changeRequestStatus = order?.openEnquiriesResponse?.changeRequestStatus
                                orderObj?.pocFirstName = order?.openEnquiriesResponse?.pocFirstName
                                orderObj?.productCategoryHistoryId = order?.openEnquiriesResponse?.productCategoryHistoryId
                                orderObj?.warpYarnHistoryId = order?.openEnquiriesResponse?.warpYarnHistoryId
                                orderObj?.weftYarnHistoryId = order?.openEnquiriesResponse?.weftYarnHistoryId
                                Log.e("OrderDetails","444444444444444444")
                                orderObj?.extraWeftYarnHistoryId = order?.openEnquiriesResponse?.extraWeftYarnHistoryId
                                orderObj?.productStatusHistoryId = order?.openEnquiriesResponse?.productStatusHistoryId
                                orderObj?.madeWittAnthranHistory = order?.openEnquiriesResponse?.madeWittAnthranHistory
                                orderObj?.buyerRatingDone = order?.openEnquiriesResponse?.buyerRatingDone
                                orderObj?.deliveryChallanUploaded = order?.openEnquiriesResponse?.deliveryChallanUploaded
                                orderObj?.deliveryChallanLabel = order?.openEnquiriesResponse?.deliveryChallanLabel
                                orderObj?.historyProductId = order?.openEnquiriesResponse?.historyProductId
                                orderObj?.madeWittAnthran = order?.openEnquiriesResponse?.madeWittAnthran
                                orderObj?.productHistoryImages = order?.openEnquiriesResponse?.productHistoryImages
                                orderObj?.enquiryStatusId= order?.openEnquiriesResponse?.enquiryStatusId
                                orderObj?.pocLastName = order?.openEnquiriesResponse?.pocLastName
                                orderObj?.pocEmail = order?.openEnquiriesResponse?.pocEmail
                                orderObj?.pocContact = order?.openEnquiriesResponse?.pocContact
                                orderObj?.gst = order?.openEnquiriesResponse?.gst
                                orderObj?.productHistoryCode = order?.openEnquiriesResponse?.productHistoryCode
                                orderObj?.productHistoryName = order?.openEnquiriesResponse?.productHistoryName
                                orderObj?.lastUpdated = order?.openEnquiriesResponse?.lastUpdated
                                orderObj?.state = order?.openEnquiriesResponse?.state
                                orderObj?.country = order?.openEnquiriesResponse?.country
                                orderObj?.brandName = order?.brandName //todo : to be changed
                                orderObj?.isMoqSend = order?.openEnquiriesResponse?.isMoqSend?:0
                                orderObj?.isPiSend = order?.openEnquiriesResponse?.isPiSend?:0
                                orderObj?.isMoqRejected = order?.isMoqRejected?:0
                                orderObj?.changeRequestOn = order?.openEnquiriesResponse?.changeRequestOn
                                orderObj?.isBlue = order?.isBlue?:0
                                orderObj?.isOrderFromCompleted=isCompleted
                                Log.e("OrderDetails","enquiryStageId: "+order?.openEnquiriesResponse?.enquiryStageId)
                                realm.copyToRealmOrUpdate(orderObj)
                            }
                        }

                    }
                }catch (e:Exception){
                    Log.e("InsertOnEnquiry",e.printStackTrace().toString())
                }
            }
//            deleteOrders(idList,isCompleted)
        }

        fun deleteOrders(idList : List<Long>?,isCompleted:Long){
            val realm = CXRealmManager.getRealmInstance()
            var ordersRealm: RealmResults<Orders>? = null
            try {
                realm?.executeTransaction {
                    ordersRealm= realm.where(Orders::class.java).equalTo(Orders.COLUMN_IS_COMPLETED,isCompleted).findAll()
                    ordersRealm?.forEach {
                        if(!idList!!.contains(it.enquiryId)){
                            //todo delete
                            val deletOrders=realm.where(Orders::class.java).equalTo(Orders.COLUMN_ENQUIRY_ID,it.enquiryId).findAll()
                            deletOrders.deleteAllFromRealm()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Orders", " Exception: ${e}")
            } finally {
//            realm.close()
            }
        }

        fun getAllOngoingOrders(): RealmResults<Orders>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(Orders::class.java)  .equalTo(Orders.COLUMN_IS_COMPLETED,0L).sort(Orders.COLUMN_LAST_UPDATED, Sort.DESCENDING).findAll()
        }

        fun getAllCompletedOrders(): RealmResults<Orders>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(Orders::class.java)  .equalTo(Orders.COLUMN_IS_COMPLETED,1L).sort(Orders.COLUMN_LAST_UPDATED, Sort.DESCENDING).findAll()
        }

        fun getSingleOnGoOrderDetails(enquiryId : Long?,isCompleted: Long): Orders? {
            var realm = CXRealmManager.getRealmInstance()
            var orders : Orders?= null
            realm.executeTransaction {
                try{
                    orders = realm.where(Orders::class.java)
                        .equalTo(Orders.COLUMN_ENQUIRY_ID,enquiryId)
                        .and()
                        .equalTo(Orders.COLUMN_IS_COMPLETED,isCompleted)
                        .limit(1)
                        .findFirst()
                }catch (e:Exception){
                    Log.e("OrderDetails","Exception : "+e.printStackTrace())
                }
            }
            return orders
        }

        fun updateCrStatus(enquiryId: Long?){
            var realm = CXRealmManager.getRealmInstance()
            var orders : Orders?= null
            realm.executeTransaction {
                try{
                    orders = realm.where(Orders::class.java)
                        .equalTo(Orders.COLUMN_ENQUIRY_ID,enquiryId)
                        .limit(1)
                        .findFirst()
                    Log.e("Toggle","orders 1111: ${orders?.changeRequestOn}")
                    orders?.let {
                        orders?.changeRequestOn=1L
                        orders?.actionMarkCr=0L
                        realm.copyToRealmOrUpdate(orders)
                    }
                    Log.e("Toggle","orders 2222: ${orders?.changeRequestOn}")
                }catch (e:Exception){
                    Log.e("Toggle","Exception : "+e.printStackTrace())
                }
            }
        }
        fun updateCrStatusForOffline(enquiryId: Long?){
            var realm = CXRealmManager.getRealmInstance()
            var orders : Orders?= null
            realm.executeTransaction {
                try{
                    orders = realm.where(Orders::class.java)
                        .equalTo(Orders.COLUMN_ENQUIRY_ID,enquiryId)
                        .limit(1)
                        .findFirst()
                    Log.e("Toggle","update 1111: ${orders?.changeRequestOn}")
                    orders?.let {
                        orders?.changeRequestOn=1L
                        orders?.actionMarkCr=1L
                        realm.copyToRealmOrUpdate(orders)
                    }
                    Log.e("Toggle","update 2222: ${orders?.changeRequestOn}")
                }catch (e:Exception){
                    Log.e("Toggle","Exception : "+e.printStackTrace())
                }
            }
        }

        fun getOrderMarkedForActions(actionsMarked:String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId=ArrayList<Long>()
            try {
                realm.executeTransaction {
                    var message1 =realm.where(Orders::class.java)
                        .equalTo(Orders.COLUMN_ACTION_MARK_CR,1L)
                        .findAll()
                    Log.e(TAG,"message1 :${message1?.count()}")
                    if(message1!=null){
                        val iterator=message1.iterator()
                        while (iterator.hasNext()) {
                            val id=iterator.next()._id?:0L
                            Log.e(TAG,"itemId :${id}")
                            itemId.add(id)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e(TAG,"while fetching actions : "+e.message)
            } finally {
//                realm.close()
            }
            return itemId
        }

        fun getEnquiryId(_id:Long): Long {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(Orders::class.java).equalTo(Orders.COLUMN__ID,_id).limit(1).findFirst()?.enquiryId?:0
        }
    }
}