package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.Enquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Transactions
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.TransactionResponse
import io.realm.RealmResults
import io.realm.Sort
import java.lang.Exception

class TransactionPredicates {

    companion object {
        private var nextID: Long? = 0


        fun insertOngoingTransactions(transacDetails: TransactionResponse, transactionStatus : Boolean) {
            val realm = CXRealmManager.getRealmInstance()
            var tranItr = transacDetails.data?.iterator()
            realm.executeTransaction {
                try {
                    if (tranItr != null) {
                        while (tranItr.hasNext()) {
                            var transac = tranItr.next()
                            var tranObj = realm.where(Transactions::class.java)
                                .equalTo(
                                    Transactions.COLUMN_TRANSACTION_ID,
                                    transac.transactionOngoing?.id
                                )
                                .limit(1)
                                .findFirst()

                            if (tranObj == null) {
                                var primId = it.where(Transactions::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exTra = it.createObject(
                                    Transactions::class.java,
                                    nextID
                                )
                                exTra?.transactionID = transac?.transactionOngoing?.id
                                exTra?.enquiryID = transac?.transactionOngoing?.enquiryId
                                exTra?.enquiryCode = transac?.enquiryCode
                                exTra?.orderCode = transac?.orderCode

                                exTra?.piID = transac?.transactionOngoing?.piId
                                exTra?.paymentID = transac?.transactionOngoing?.paymentId
                                exTra?.taxInvoiceID = transac?.transactionOngoing?.taxInvoiceId
                                exTra?.challanID = transac?.transactionOngoing?.challanId

                                exTra?.piHistoryID = transac?.transactionOngoing?.piHistoryId
                                exTra?.receiptID = transac?.transactionOngoing?.receiptId
                                exTra?.eta = transac?.eta

                                exTra?.percentage = transac?.transactionOngoing?.percentage
                                exTra?.paidAmount = transac?.transactionOngoing?.paidAmount
                                exTra?.totalAmount = transac?.transactionOngoing?.totalAmount

                                exTra?.upcomingStatus = transac?.transactionOngoing?.upcomingStatus
                                exTra?.accomplishedStatus =
                                    transac?.transactionOngoing?.accomplishedStatus
                                exTra?.isActionCompleted =
                                    transac?.transactionOngoing?.isActionCompleted
                                exTra?.isActive = transac?.transactionOngoing?.isActive

                                exTra?.transactionOn = transac?.transactionOngoing?.transactionOn
                                exTra?.modifiedOn = transac?.transactionOngoing?.modifiedOn
                                exTra?.completedOn = transac?.transactionOngoing?.completedOn

                                exTra?.isCompleted = transactionStatus

                                realm.copyToRealmOrUpdate(exTra)
                            } else {
                                nextID = tranObj?._id ?: 0

                                tranObj?.transactionID = transac?.transactionOngoing?.id
                                tranObj?.enquiryID = transac?.transactionOngoing?.enquiryId
                                tranObj?.enquiryCode = transac?.enquiryCode
                                tranObj?.orderCode = transac?.orderCode

                                tranObj?.piID = transac?.transactionOngoing?.piId
                                tranObj?.paymentID = transac?.transactionOngoing?.paymentId
                                tranObj?.taxInvoiceID = transac?.transactionOngoing?.taxInvoiceId
                                tranObj?.challanID = transac?.transactionOngoing?.challanId

                                tranObj?.piHistoryID = transac?.transactionOngoing?.piHistoryId
                                tranObj?.receiptID = transac?.transactionOngoing?.receiptId
                                tranObj?.eta = transac?.eta

                                tranObj?.percentage = transac?.transactionOngoing?.percentage
                                tranObj?.paidAmount = transac?.transactionOngoing?.paidAmount
                                tranObj?.totalAmount = transac?.transactionOngoing?.totalAmount

                                tranObj?.upcomingStatus =
                                    transac?.transactionOngoing?.upcomingStatus
                                tranObj?.accomplishedStatus =
                                    transac?.transactionOngoing?.accomplishedStatus
                                tranObj?.isActionCompleted =
                                    transac?.transactionOngoing?.isActionCompleted
                                tranObj?.isActive = transac?.transactionOngoing?.isActive

                                tranObj?.transactionOn = transac?.transactionOngoing?.transactionOn
                                tranObj?.modifiedOn = transac?.transactionOngoing?.modifiedOn
                                tranObj?.completedOn = transac?.transactionOngoing?.completedOn

                                tranObj?.isCompleted = transactionStatus

                                realm.copyToRealmOrUpdate(tranObj)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("InsertTransaction", e.printStackTrace().toString())
                }
            }
        }

        fun insertCompletedTransactions(transacDetails: TransactionResponse, transactionStatus : Boolean) {
            val realm = CXRealmManager.getRealmInstance()
            var tranItr = transacDetails.data?.iterator()
            realm.executeTransaction {
                try {
                    if (tranItr != null) {
                        while (tranItr.hasNext()) {
                            var transac = tranItr.next()
                            var tranObj = realm.where(Transactions::class.java)
                                .equalTo(Transactions.COLUMN_TRANSACTION_ID, transac.transactionCompleted?.id)
                                .limit(1)
                                .findFirst()

                            if (tranObj == null) {
                                var primId = it.where(Transactions::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exTra = it.createObject(
                                    Transactions::class.java,
                                    nextID
                                )
                                exTra?.transactionID = transac?.transactionCompleted?.id
                                exTra?.enquiryID = transac?.transactionCompleted?.enquiryId
                                exTra?.enquiryCode = transac?.enquiryCode
                                exTra?.orderCode = transac?.orderCode

                                exTra?.piID = transac?.transactionCompleted?.piId
                                exTra?.paymentID = transac?.transactionCompleted?.paymentId
                                exTra?.taxInvoiceID = transac?.transactionCompleted?.taxInvoiceId
                                exTra?.challanID = transac?.transactionCompleted?.challanId

                                exTra?.piHistoryID = transac?.transactionCompleted?.piHistoryId
                                exTra?.receiptID = transac?.transactionCompleted?.receiptId
                                exTra?.eta = transac?.eta

                                exTra?.percentage = transac?.transactionCompleted?.percentage
                                exTra?.paidAmount = transac?.transactionCompleted?.paidAmount
                                exTra?.totalAmount = transac?.transactionCompleted?.totalAmount

                                exTra?.upcomingStatus = transac?.transactionCompleted?.upcomingStatus
                                exTra?.accomplishedStatus = transac?.transactionCompleted?.accomplishedStatus
                                exTra?.isActionCompleted = transac?.transactionCompleted?.isActionCompleted
                                exTra?.isActive = transac?.transactionCompleted?.isActive

                                exTra?.transactionOn = transac?.transactionCompleted?.transactionOn
                                exTra?.modifiedOn = transac?.transactionCompleted?.modifiedOn
                                exTra?.completedOn = transac?.transactionCompleted?.completedOn

                                exTra?.isCompleted = transactionStatus

                                realm.copyToRealmOrUpdate(exTra)
                            } else {
                                nextID = tranObj?._id ?: 0

                                tranObj?.transactionID = transac?.transactionCompleted?.id
                                tranObj?.enquiryID = transac?.transactionCompleted?.enquiryId
                                tranObj?.enquiryCode = transac?.enquiryCode
                                tranObj?.orderCode = transac?.orderCode

                                tranObj?.piID = transac?.transactionCompleted?.piId
                                tranObj?.paymentID = transac?.transactionCompleted?.paymentId
                                tranObj?.taxInvoiceID = transac?.transactionCompleted?.taxInvoiceId
                                tranObj?.challanID = transac?.transactionCompleted?.challanId

                                tranObj?.piHistoryID = transac?.transactionCompleted?.piHistoryId
                                tranObj?.receiptID = transac?.transactionCompleted?.receiptId
                                tranObj?.eta = transac?.eta

                                tranObj?.percentage = transac?.transactionCompleted?.percentage
                                tranObj?.paidAmount = transac?.transactionCompleted?.paidAmount
                                tranObj?.totalAmount = transac?.transactionCompleted?.totalAmount

                                tranObj?.upcomingStatus = transac?.transactionCompleted?.upcomingStatus
                                tranObj?.accomplishedStatus = transac?.transactionCompleted?.accomplishedStatus
                                tranObj?.isActionCompleted = transac?.transactionCompleted?.isActionCompleted
                                tranObj?.isActive = transac?.transactionCompleted?.isActive

                                tranObj?.transactionOn = transac?.transactionCompleted?.transactionOn
                                tranObj?.modifiedOn = transac?.transactionCompleted?.modifiedOn
                                tranObj?.completedOn = transac?.transactionCompleted?.completedOn

                                tranObj?.isCompleted = transactionStatus

                                realm.copyToRealmOrUpdate(tranObj)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("InsertTransaction", e.printStackTrace().toString())
                }
            }
        }


        fun getAllOngoingTransactions(): RealmResults<Transactions>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(Transactions::class.java)
                .equalTo(Transactions.COLUMN_IS_COMPLETED,false)
                .sort(Transactions.COLUMN_MODIFIED_ON, Sort.DESCENDING)
                .findAll()
        }

        fun getAllCompletedTransactions(): RealmResults<Transactions>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(Transactions::class.java)
                .equalTo(Transactions.COLUMN_IS_COMPLETED,true)
                .sort(Transactions.COLUMN_MODIFIED_ON, Sort.DESCENDING)
                .findAll()
        }

        fun getFilteredTransactions(paymentType : String, transactionStatus : Boolean): RealmResults<Transactions>? {
            val realm = CXRealmManager.getRealmInstance()
            var tranObj : RealmResults<Transactions> ?= null
            realm?.executeTransaction {
                try {
                    when(paymentType){
                        "All" -> {
                            tranObj = realm?.where(Transactions::class.java)
                            .equalTo(Transactions.COLUMN_IS_COMPLETED,transactionStatus)
                            .findAll()
                        }

                        "PI ID" -> {
                            tranObj = realm?.where(Transactions::class.java)
                            .notEqualTo(Transactions.COLUMN_PI_ID,0L)
                            .and()
                            .equalTo(Transactions.COLUMN_IS_COMPLETED,transactionStatus)
                            .findAll()
                        }

                        "Payment ID" -> {
                            tranObj = realm?.where(Transactions::class.java)
                                .notEqualTo(Transactions.COLUMN_PAYMENT_ID,0L)
                                .and()
                                .equalTo(Transactions.COLUMN_IS_COMPLETED,transactionStatus)
                                .findAll()
                        }

                        "Tax Invoice ID" -> {
                            tranObj = realm?.where(Transactions::class.java)
                                .notEqualTo(Transactions.COLUMN_TAXINVOICE_ID,0L)
                                .and()
                                .equalTo(Transactions.COLUMN_IS_COMPLETED,transactionStatus)
                                .findAll()
                        }

                        "Challan ID" -> {
                            tranObj = realm?.where(Transactions::class.java)
                                .notEqualTo(Transactions.COLUMN_CHALLAN_ID,0L)
                                .and()
                                .equalTo(Transactions.COLUMN_IS_COMPLETED,transactionStatus)
                                .findAll()
                        }
                    }
                }catch (e:Exception){
                    Log.e("Transactions",e.printStackTrace().toString())
                }
            }
            return tranObj
        }

        fun getTransactionByEnquiryId(searchString : Long): RealmResults<Transactions>? {
            val realm = CXRealmManager.getRealmInstance()
            var tranObj : RealmResults<Transactions> ?= null
            realm?.executeTransaction {
                try {
               tranObj = realm?.where(Transactions::class.java).equalTo(Transactions.COLUMN_ENQUIRY_ID,searchString).findAll()
                }catch (e:Exception){
                    Log.e("Transactions",e.printStackTrace().toString())
                }
            }
            return tranObj
        }


    }
}