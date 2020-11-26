package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.transaction

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
import com.adrosonic.craftexchangemarketing.databinding.ActivityTransactionShowBinding
import com.adrosonic.craftexchangemarketing.databinding.ActivityTransactionShowBindingImpl
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

fun Context.showTransactionIntent(enquiryID: Long, type: Long): Intent {
    val intent = Intent(this, ShowTransactionActivity::class.java)
    intent.putExtra("enquiryID", enquiryID)
    intent.putExtra("type", type)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}
class ShowTransactionActivity: AppCompatActivity(),
    TransactionViewModel.TransactionInterface
    {
    private var mBinding : ActivityTransactionShowBinding?= null
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
        mTranVM.transactionListener =this

        Utility.getDeliveryTimeList()?.let { moqDeliveryTimeList.addAll(it) }

        if (intent.extras != null) {
            enquiry = intent.getLongExtra("enquiryID", 0)

            Log.d("type", "onCreate: " + type)

        }
        Log.d("type", "onCreate: " + type)


        if (Utility.checkIfInternetConnected(this)) {


                mTranVM.getSingleOngoingTransactions(enquiry!!)





        } else {
            Utility.displayMessage(getString(R.string.no_internet_connection), this)
//            }
        }



        mBinding = ActivityTransactionShowBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)


        //Transactions


        //TransactionsEnd

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