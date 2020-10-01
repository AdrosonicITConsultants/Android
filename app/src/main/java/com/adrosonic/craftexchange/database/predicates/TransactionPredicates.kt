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


        fun insertTransactions(transacDetails: TransactionResponse) {
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
                .equalTo(Transactions.COLUMN_IS_ACTIVE,1L)
                .sort(Transactions.COLUMN_MODIFIED_ON, Sort.DESCENDING)
                .findAll()
        }

        fun getFilteredOngoTransactions(paymentType : String): RealmResults<Transactions>? {
            val realm = CXRealmManager.getRealmInstance()
            var tranObj : RealmResults<Transactions> ?= null
            realm?.executeTransaction {
                try {
                    when(paymentType){
                        "All" -> { tranObj = realm?.where(Transactions::class.java).findAll() }
                        "PI ID" -> { tranObj = realm?.where(Transactions::class.java).notEqualTo(Transactions.COLUMN_PI_ID,0L).findAll() }
                        "Payment ID" -> { tranObj = realm?.where(Transactions::class.java).notEqualTo(Transactions.COLUMN_PAYMENT_ID,0L).findAll() }
                        "Tax Invoice ID" -> { tranObj = realm?.where(Transactions::class.java).notEqualTo(Transactions.COLUMN_TAXINVOICE_ID,0L).findAll() }
                        "Challan ID" -> { tranObj = realm?.where(Transactions::class.java).notEqualTo(Transactions.COLUMN_CHALLAN_ID,0L).findAll() }
                    }
                }catch (e:Exception){
                    Log.e("Transactions",e.printStackTrace().toString())
                }
            }
            return tranObj
        }

    }
}