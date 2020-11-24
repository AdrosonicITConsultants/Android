package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Orders
import com.adrosonic.craftexchangemarketing.database.predicates.MoqsPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.TransactionPredicates
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndEnquiryDetailsBinding
import com.adrosonic.craftexchangemarketing.enums.AvailableStatus
import com.adrosonic.craftexchangemarketing.enums.getId
import com.adrosonic.craftexchangemarketing.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchangemarketing.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryData
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Datum
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.chat.chatLogDetailsIntent
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.cr.crContext
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.escalations.chatEscalationIntent
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.qC.qcFormIntent
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.transaction.adapter.OnGoingTransactionRecyclerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.transaction.raiseTaxInvIntent
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanProfileActivity
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.BuyerProfileActivity
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryViewModel
import com.adrosonic.craftexchangemarketing.viewModels.OrdersViewModel
import com.adrosonic.craftexchangemarketing.viewModels.TransactionViewModel
import com.google.gson.GsonBuilder

fun Context.enquiryDetailsIntent(enquiryID: Long, type: Long): Intent {
    val intent = Intent(this, EnquiryDetailsActivity::class.java)
    intent.putExtra("enquiryID", enquiryID)
    intent.putExtra("type", type)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}
class EnquiryDetailsActivity: AppCompatActivity(),
    EnquiryViewModel.BuyersMoqInterface ,
    TransactionViewModel.TransactionInterface,
OrdersViewModel.FetchOrderInterface{
    private var mBinding : ActivityIndEnquiryDetailsBinding?= null
    private var mUserConfig = UserConfig()
    var enquiryData :String?=null
    var enquiryRes : EnquiryData ?= null
    var type : Long?=1
    var enquiry : Long?=null
    var moqDeliveryTimeList=ArrayList<Datum>()
    val mEnqVM : EnquiryViewModel by viewModels()
    val mTranVM : TransactionViewModel by viewModels()
    val mOrderVm : OrdersViewModel by viewModels()
    var orderDetails : Orders?= null
    var changeRequestOn : Long? = null
    var changeRequestStatus : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Utility.getDeliveryTimeList()?.let { moqDeliveryTimeList.addAll(it) }

        if (intent.extras != null) {
            enquiry = intent.getLongExtra("enquiryID", 0)
            type = intent.getLongExtra("type", 1)
            Log.d("type", "onCreate: " + type)

        }
        Log.d("type", "onCreate: " + type)


        if (Utility.checkIfInternetConnected(this)) {

            mEnqVM.getMoqs(enquiry!!)
            if (type!! == 3L) {
                mOrderVm.getSingleOngoingOrder(enquiry!!)
                mTranVM.getSingleOngoingTransactions(enquiry!!)
                mOrderVm.getChangeRequestDetails(enquiry!!)
                Log.d("type", "onCreate: " + type)

            } else if (type!! == 4L || type!! == 5L) {
                mOrderVm.getSingleCompletedOrder(enquiry!!)
                mTranVM.getSingleCompletedTransactions(enquiry!!)
                mOrderVm.getChangeRequestDetails(enquiry!!)
                Log.d("type", "onCreate: " + type)

            }


        } else {
            Utility.displayMessage(getString(R.string.no_internet_connection), this)
//            }
        }
        mEnqVM.buyerMoqListener = this
        mOrderVm.fetchEnqListener = this

        enquiryData = mUserConfig.enquiryData.toString()
        val gson = GsonBuilder().create()
        enquiryRes = gson.fromJson(enquiryData, EnquiryData::class.java)
        Log.d("indEnq", "onCreate: " + enquiryRes)
        mBinding = ActivityIndEnquiryDetailsBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        if (enquiryRes?.buyerId == null)
            mBinding?.orderTime1?.text = "Not available"
        else
            mBinding?.orderTime1?.text = ""
        if (enquiryRes?.artisanId == null)
            mBinding?.orderTime11?.text = "Not available"
        else
            mBinding?.orderTime11?.text = ""



        mBinding?.buyerDetailsLayer?.setOnClickListener {
            if (enquiryRes?.buyerId != null) {
                val myIntent = Intent(this, BuyerProfileActivity::class.java)
                myIntent.putExtra("buyerId", enquiryRes?.buyerId!!)
                startActivity(myIntent)
            }
        }
        mBinding?.artisanDetailsLayer?.setOnClickListener {
            if (enquiryRes?.artisanId != null) {
                val myIntent = Intent(this, ArtisanProfileActivity::class.java)
                myIntent.putExtra("artisanId", enquiryRes?.artisanId!!)
                startActivity(myIntent)
            }
        }
        if (enquiryRes?.currenStageId!! > 7) {
            mBinding?.taxInvoiceAvail?.text = "View"

        } else {
            mBinding?.taxInvoiceAvail?.text = "Not Available"
        }
        mBinding?.taxInvoiceLayer?.setOnClickListener {
            if (enquiryRes?.currenStageId!! > 7) {
                enquiry?.let { startActivity(this.raiseTaxInvIntent(it, true, SendTiRequest())) }

            }
        }
        if (enquiryRes?.innerCurrenStage == null) {
            mBinding?.currentStage?.text = enquiryRes?.currenStage
        } else {
            mBinding?.currentStage?.text = enquiryRes?.innerCurrenStage
        }

        if (enquiryRes?.currenStageId!! < 3) {
            mBinding?.orderTime111?.text = "Not Available"
        } else {
            mBinding?.orderTime111?.text = ""
        }

        mBinding?.chatDetailsLayer?.setOnClickListener {
            startActivity(enquiry?.let { it1 -> this.chatLogDetailsIntent(it1) })
        }
        mBinding?.escalationDetailsLayer?.setOnClickListener {
            startActivity(enquiry?.let { it1 -> this.chatEscalationIntent(it1) })
        }
        mBinding?.piDetailsLayer?.setOnClickListener {
            if (enquiryRes?.currenStageId!! < 3) {
            } else {
                enquiry?.let {
                    startActivity(
                        this.raisePiContext(
                            it,
                            true,
                            SendPiRequest(),
                            false
                        )
                    )
                }
            }
        }

        if (type!! == 3L) {
            mBinding?.qualityCheckLayer?.setOnClickListener {
                if(enquiryRes?.currenStageId!!>6)
                {
                    startActivity(
                        this?.qcFormIntent()
                            ?.putExtra(ConstantsDirectory.ENQUIRY_ID, enquiry!!)
                            ?.putExtra(ConstantsDirectory.ORDER_STATUS_FLAG, 0L)
                    )
                }
                else{
                    Utility.displayMessage(
                        "Quality Check not done by artisan yet.",
                        this
                    )
                }

            }
        } else if (type!! == 4L || type!! == 5L) {
            mBinding?.qualityCheckLayer?.setOnClickListener {
                if(enquiryRes?.currenStageId!!>6)
                {
                    startActivity(
                        this?.qcFormIntent()
                            ?.putExtra(ConstantsDirectory.ENQUIRY_ID, enquiry!!)
                            ?.putExtra(ConstantsDirectory.ORDER_STATUS_FLAG, 1L)
                    )
                }
                else{
                    Utility.displayMessage(
                        "Quality Check not done by artisan yet.",
                        this
                    )
                }
            }

        }




        mBinding?.moqDetailsLayer?.setOnClickListener {
            handleMoqVisiblities()
        }
        mBinding?.ProductName?.setOnClickListener {
//            Utility.displayMessage("Product Page Opens Here")
        }
        mBinding?.EnquiryCode?.text = enquiryRes?.code
        mBinding?.ArtisanBrand?.text = enquiryRes?.artisanBrand
        mBinding?.BuyerBrand?.text = enquiryRes?.buyerBrand
        if (enquiryRes?.amount == null) {
            mBinding?.Amount?.text = "NA"
        } else {
            mBinding?.Amount?.text = enquiryRes?.amount.toString()
        }
        val date = enquiryRes?.dateStarted?.split("T")?.get(0)
        mBinding?.dateStarted?.text = date
        if (enquiryRes?.lastUpdated == null) {
            mBinding?.lastUpdated?.text = "NA"
        } else {
            val date = enquiryRes?.lastUpdated?.split("T")?.get(0)
            mBinding?.lastUpdated?.text = date
        }
        if (enquiryRes?.eta == null) {
            mBinding?.ETA?.text = "NA"
        } else {
            val date = enquiryRes?.eta?.split("T")?.get(0)
            mBinding?.ETA?.text = date
        }



        mBinding?.changeRequestLayer?.setOnClickListener {
            if(changeRequestStatus == null)
            {
                changeRequestStatus = 4L
            }
            if(changeRequestOn == null)
            {
                changeRequestOn = 1L
            }
            Log.d("changeRequest", "onCreate: " + changeRequestOn +""+ changeRequestStatus)
            Log.d("changeRequest", "onCreate: " + AvailableStatus.MADE_TO_ORDER.getId())
            if (enquiryRes?.productHistoryId != null) {
                when (enquiryRes?.productHistoryStatus) {
                    0 -> {
                        mBinding?.typeProduct?.text = "Buyer Custom Product"
                        if (changeRequestOn!!.toLong() == 1L) {
                            when (changeRequestStatus!!.toLong()) {
                                0L -> {
                                    //waiting for ack
                                    enquiry?.let { startActivity(this.crContext(it, 0L)) }
                                }
                                1L -> enquiry?.let { startActivity(this.crContext(it, 1L)) }
                                2L -> enquiry?.let { startActivity(this.crContext(it, 2L)) }
                                3L -> enquiry?.let { startActivity(this.crContext(it, 3L)) }
                                else -> {
                                    Utility.displayMessage(
                                        "Change request not raised by buyer.",
                                        this
                                    )
//
                                }
                            }
                        } else Utility.displayMessage("Change request disabled by artisan.", this)


                    }
                    1 -> {
                        mBinding?.typeProduct?.text = "Made to Order"
                        if (changeRequestOn!!.toLong() == 1L) {
                            when (changeRequestStatus!!.toLong()) {
                                0L -> {
                                    //waiting for ack
                                    enquiry?.let { startActivity(this.crContext(it, 0L)) }
                                }
                                1L -> enquiry?.let { startActivity(this.crContext(it, 1L)) }
                                2L -> enquiry?.let { startActivity(this.crContext(it, 2L)) }
                                3L -> enquiry?.let { startActivity(this.crContext(it, 3L)) }
                                else -> {
                                    Utility.displayMessage(
                                        "Change request not raised by buyer.",
                                        this
                                    )
//
                                }
                            }
                        } else Utility.displayMessage("Change request disabled by artisan.", this)


                    }
                    2 -> {
                        mBinding?.typeProduct?.text = "Available in Stock"
                        Utility.displayMessage("Change request not applicable for in stock products.", this)
                    }
                }

            } else {
                mBinding?.ProductName?.text = enquiryRes?.tag
                when (enquiryRes?.productStatus) {
                    0 -> {
                        mBinding?.typeProduct?.text = "Buyer Custom Product"
                        if (changeRequestOn!!.toLong() == 1L) {
                            when (changeRequestStatus!!.toLong()) {
                                0L -> {
                                    //waiting for ack
                                    enquiry?.let { startActivity(this.crContext(it, 0L)) }
                                }
                                1L -> enquiry?.let { startActivity(this.crContext(it, 1L)) }
                                2L -> enquiry?.let { startActivity(this.crContext(it, 2L)) }
                                3L -> enquiry?.let { startActivity(this.crContext(it, 3L)) }
                                else -> {
                                    Utility.displayMessage(
                                        "Change request not raised by buyer.",
                                        this
                                    )
//
                                }
                            }
                        } else Utility.displayMessage("Change request disabled by artisan.", this)
                    }

                    1 -> {
                        mBinding?.typeProduct?.text = "Made to Order"
                        if (changeRequestOn!!.toLong() == 1L) {
                            when (changeRequestStatus!!.toLong()) {
                                0L -> {
                                    //waiting for ack
                                    enquiry?.let { startActivity(this.crContext(it, 0L)) }
                                }
                                1L -> enquiry?.let { startActivity(this.crContext(it, 1L)) }
                                2L -> enquiry?.let { startActivity(this.crContext(it, 2L)) }
                                3L -> enquiry?.let { startActivity(this.crContext(it, 3L)) }
                                else -> {
                                    Utility.displayMessage(
                                        "Change request not raised by buyer.",
                                        this
                                    )
//
                                }
                            }
                        } else Utility.displayMessage("Change request disabled by artisan.", this)

                    }
                    2 -> {
                        mBinding?.typeProduct?.text = "Available in Stock"
                        Utility.displayMessage("Change request not applicable for in stock products.", this)

                    }
                }


            }
        }
            //Transactions
            mBinding?.viewPaymentLayer?.setOnClickListener {
                Log.d("Transaction", "onCreate:  clicked")
                if (mBinding?.transactionList!!.visibility == View.VISIBLE) mBinding?.transactionList!!.visibility =
                    View.GONE
                else mBinding?.transactionList!!.visibility = View.VISIBLE
            }


            //TransactionsEnd
            when (enquiryRes?.madeWithAntharan) {
                null -> {

                }
                0 -> {
                    mBinding?.ProductTypeImage?.setImageResource(R.drawable.ic_artisan_self_design_icon)
                }
                1 -> {
                    mBinding?.ProductTypeImage?.setImageResource(R.drawable.ic_antaran_co_design_icon)
                }

            }
            if (enquiryRes?.productHistoryId != null) {
                mBinding?.ProductName?.text = enquiryRes?.historyTag
                when (enquiryRes?.productHistoryStatus) {
                    0 -> {
                        mBinding?.typeProduct?.text = "Buyer Custom Product"
                        mBinding?.totalSteps?.text = "/10"
                        mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                    }
                    1 -> {
                        mBinding?.typeProduct?.text = "Made to Order"
                        mBinding?.totalSteps?.text = "/10"
                        mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                    }
                    2 -> {
                        mBinding?.typeProduct?.text = "Available in Stock"
                        mBinding?.totalSteps?.text = "/7"
                        if (enquiryRes?.currenStageId!! > 4) {
                            mBinding?.stepsCompleted?.text =
                                (enquiryRes?.currenStageId!! - 3).toString()

                        } else {
                            mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()
                        }

                    }
                }

            } else {
                mBinding?.ProductName?.text = enquiryRes?.tag
                when (enquiryRes?.productStatus) {
                    0 -> {
                        mBinding?.typeProduct?.text = "Buyer Custom Product"
                        mBinding?.totalSteps?.text = "/10"
                        mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                    }
                    1 -> {
                        mBinding?.typeProduct?.text = "Made to Order"
                        mBinding?.totalSteps?.text = "/10"
                        mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                    }
                    2 -> {
                        mBinding?.typeProduct?.text = "Available in Stock"
                        mBinding?.totalSteps?.text = "/7"
                        if (enquiryRes?.currenStageId!! > 4) {
                            mBinding?.stepsCompleted?.text =
                                (enquiryRes?.currenStageId!! - 3).toString()

                        } else {
                            mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()
                        }

                    }
                }
            }
            if (type!! < 3) {
                Log.d("type", "onCreate: enter if")
                mBinding?.changeRequestLayer?.visibility = View.GONE
                mBinding?.qualityCheckLayer?.visibility = View.GONE
                mBinding?.taxInvoiceLayer?.visibility = View.GONE
                mBinding?.viewPaymentLayer?.visibility = View.GONE
            }

        }

    override fun onGetMoqCall() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
//                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("EnquiryDetails", "Exception onAddMoqSuccess " + e.message)
        }
    }
    private fun handleMoqVisiblities(){
        val moq = MoqsPredicates.getMoqs(enquiry)
        Log.d("moqcheck", "onCreate: " + moq)
        if (moq == null || moq!!.size == 0) {

        }
        else{
            if(mBinding?.moqDetails?.visibility == View.GONE){
                mBinding?.moqDetails?.visibility = View.VISIBLE
            }
            else{
                mBinding?.moqDetails?.visibility = View.GONE

            }

        }
    }
    fun setDetails(){

        var tranList = TransactionPredicates.getTransactionByEnquiryId(enquiry ?: 0)
        Log.d("Transaction", "onCreate:  clicked" + tranList)

        if (tranList!!.size > 0) {
            mBinding?.viewTransaction?.text = "View"
            mBinding?.transactionList?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val transactionAdapter =
                OnGoingTransactionRecyclerAdapter(this, tranList)
            mBinding?.transactionList?.adapter = transactionAdapter
//                    transactionAdapter.listener = this
        } else {
            mBinding?.viewTransaction?.text = "No transaction present"
        }

        val moq = MoqsPredicates.getMoqs(enquiry)
        Log.d("moqcheck", "onCreate: " + moq)
        if (moq == null || moq!!.size == 0) {
            //todo show simple empty view
            mBinding?.moqDetails?.visibility = View.GONE
//            mBinding?.moqListLayout?.visibility = View.GONE
            mBinding?.orderTime?.visibility = View.VISIBLE
            mBinding?.orderTime?.text = "No MOQs Received"
        }
        else{
            if (moq.size == 1 && moq?.get(0)?.accepted == true) {
                //todo show product vala view
                var moq1 = moq?.get(0)
//                mBinding?.moqListLayout?.visibility = View.GONE
                mBinding?.moqOrderQty?.text = "" + moq1?.moq
                mBinding?.orderQuantity?.text = "" + moq1?.moq
                mBinding?.moqOrderAmount?.text = "₹ ${moq1?.ppu}"
                mBinding?.orderAmount?.text = "₹ ${moq1?.ppu}"
                moqDeliveryTimeList?.forEach {
                    if (it.id.equals(moq1?.deliveryTimeId)) {
                        mBinding?.moqOrderEta?.text = if (it?.days.equals(0L)) {
                            "Immediate"
                        } else "${it?.days} Days"// "${it?.days} Days"
                        mBinding?.orderTime?.text = if (it?.days.equals(0L)) {
                            "Immediate"
                        } else "${it?.days} Days"//"${it?.days} Days"
                    }
                }
            }
            else {

            }
        }
    }
    override fun onSendCustomMoqSuccess(moqId: Long) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {

            })
        } catch (e: Exception) {
        }
    }

    override fun onSendCustomMoqFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
            })
        } catch (e: Exception) {
        }
    }

    override fun onGetTransactionsSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Transaction","getSingleTransactions Success")
                setDetails()
//                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("Transaction", "Exception onStatusChangeFailure " + e.message)
        }
    }
    override fun onFailure(){
        Utility.displayMessage("something went Wrong", this)

    }
    override fun onSuccess(){
        orderDetails = mOrderVm.loadSingleOrderDetails(enquiry!!,0)
        Log.d("changeRequest", "onCreate: "+orderDetails)
        changeRequestOn = orderDetails?.changeRequestOn
        changeRequestStatus = orderDetails?.changeRequestStatus
    }

    override fun onGetTransactionsFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Transaction","onGetTransactionsFailure")
//                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("Transaction", "Exception onStatusChangeFailure " + e.message)
        }
    }

}