package com.adrosonic.craftexchange.ui.modules.transaction

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.databinding.ActivityRaisePiBinding
import com.adrosonic.craftexchange.databinding.ActivityViewDocumentBinding
import com.adrosonic.craftexchange.enums.DocumentType
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.RaisePiActivity
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler

fun Context.viewDocument(enquiryId:Long, documentType:Long): Intent {
    val intent = Intent(this, ViewDocumentActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("documentType", documentType)
    return intent
}

class ViewDocumentActivity : LocaleBaseActivity(), TransactionViewModel.PaymentReceiptInterface {

    private var mBinding: ActivityViewDocumentBinding? = null
    val mTranVM : TransactionViewModel by viewModels()
    val mOrdVM : OrdersViewModel by viewModels()

    var enqID : Long?= 0L
    var docType : Long?=0L
    var orderDetails : Orders?= null
    var url : String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityViewDocumentBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        if (intent.extras != null) {
            enqID = intent.getLongExtra("enquiryId",0)
            docType = intent.getLongExtra("documentType",0)
        }

        mTranVM?.paymentReceiptListener=this

        if(Utility.checkIfInternetConnected(this)){

            when(docType){
                DocumentType.ADVANCEPAY.getId() -> {
                    enqID?.let { mTranVM?.getAdvancePaymentReceipt(it) }
                }
                DocumentType.FINALPAY.getId() -> {
                    enqID?.let { mTranVM?.getFinalPaymentReceipt(it) }
                }
                DocumentType.REVADVANCEPAY.getId() -> {
                    enqID?.let { mTranVM?.getRevisedAdvancedPaymentReceipt(it) }
                }
                DocumentType.DELIVERY_CHALLAN.getId() -> {
                    orderDetails = enqID?.let { mOrdVM.loadSingleOrderDetails(it,0) }
                    url = Utility.getDeliveryChallanReceiptUrl(enqID,orderDetails?.deliveryChallanLabel)
                    mBinding?.loader?.let { mBinding?.documentView?.let { it1 -> url?.let { it2 ->
                        ImageSetter.setFullImage(this,
                            it2, it1, it)
                    } }
                    }
                }

            }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),this)
        }

        mBinding?.btnBack?.setOnClickListener {
            onBackPressed()
        }

        mBinding?.documentView?.setOnTouchListener(ImageMatrixTouchHandler(this))

    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("PaymentReceipt", "OnFailure")
            }
            )
        } catch (e: Exception) {
            Log.e("PaymentReceipt", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess(imgName: String, receiptId : Long) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("PaymentReceipt", "OnSuccess")
                when(docType){
                    DocumentType.ADVANCEPAY.getId() -> {
                        url = Utility.getAdvancePaymentImageUrl(receiptId,imgName)
                    }
                    DocumentType.REVADVANCEPAY.getId() -> {
                        url = Utility.getAdvancePaymentImageUrl(receiptId,imgName)
                    }
                    DocumentType.FINALPAY.getId() -> {
                        url = Utility.getFinalPaymentImageUrl(receiptId,imgName)
                    }
                 }
                mBinding?.loader?.let { mBinding?.documentView?.let { it1 -> url?.let { it2 ->
                    ImageSetter.setFullImage(this,
                        it2, it1, it)
                } }
                }
            }
            )
        } catch (e: Exception) {
            Log.e("PaymentReceipt", "Exception OnSuccess " + e.message)
        }
    }
}