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
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.adapter.MoqAdapter
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.MoqsPredicates
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndEnquiryDetailsBinding
import com.adrosonic.craftexchangemarketing.databinding.EnquiryDatabaseActivityBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryData
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Datum
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryViewModel
import com.github.bassaer.chatmessageview.model.Message
import com.github.bassaer.chatmessageview.view.MessageView
import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun Context.enquiryDetailsIntent(enquiryID: Long, type : Long): Intent {
    val intent = Intent(this, EnquiryDetailsActivity::class.java)
    intent.putExtra("enquiryID", enquiryID)
    intent.putExtra("type", type)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}
class EnquiryDetailsActivity: AppCompatActivity(),
    EnquiryViewModel.BuyersMoqInterface {
    private var mBinding : ActivityIndEnquiryDetailsBinding?= null
    private var mUserConfig = UserConfig()
    var enquiryData :String?=null
    var enquiryRes : EnquiryData ?= null
    var type : Long?=1
    var enquiry : Long?=null
    var moqDeliveryTimeList=ArrayList<Datum>()
    val mEnqVM : EnquiryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.getDeliveryTimeList()?.let {moqDeliveryTimeList.addAll(it)  }

        if(intent.extras!=null){
            enquiry = intent.getLongExtra("enquiryID", 0)
            type = intent.getLongExtra("type", 1)

        }
        if(Utility.checkIfInternetConnected(this)){
//            enqID?.let { mEnqVM.getSingleOngoingEnquiry(it) }
//            viewLoader()
            mEnqVM.getMoqs(enquiry!!)
//            mEnqVM?.getSinglePi(enqID!!)
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),this)
//            var enqDbDetails = EnquiryPredicates.getSingleOnGoEnquiryDetails(enquiry)
//            if(enqDbDetails!= null){
//                setDetails()
//            }
        }
        mEnqVM.buyerMoqListener = this

        enquiryData = mUserConfig.enquiryData.toString()
        val gson = GsonBuilder().create()
        enquiryRes = gson.fromJson(enquiryData, EnquiryData::class.java)
        Log.d("indEnq", "onCreate: " +enquiryRes )
        mBinding = ActivityIndEnquiryDetailsBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        if(enquiryRes?.innerCurrenStage == null)
        {
            mBinding?.currentStage?.text = enquiryRes?.currenStage
        }
        else{
            mBinding?.currentStage?.text = enquiryRes?.innerCurrenStage

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
        if(enquiryRes?.amount == null)
        {
            mBinding?.Amount?.text = "NA"
        }
        else{
            mBinding?.Amount?.text = enquiryRes?.amount.toString()
        }
        val date = enquiryRes?.dateStarted?.split("T")?.get(0)
        mBinding?.dateStarted?.text = date
        if(enquiryRes?.lastUpdated == null )
        {
            mBinding?.lastUpdated?.text = "NA"
        }
        else{
            val date = enquiryRes?.lastUpdated?.split("T")?.get(0)
            mBinding?.lastUpdated?.text = date
        }
        if(enquiryRes?.eta == null )
        {
            mBinding?.ETA?.text = "NA"
        }
        else{
            val date = enquiryRes?.eta?.split("T")?.get(0)
            mBinding?.ETA?.text = date
        }
        when(enquiryRes?.madeWithAntharan){
            null->{

            }
            0->{
                mBinding?.ProductTypeImage?.setImageResource(R.drawable.ic_artisan_self_design_icon)
            }
            1->{
                mBinding?.ProductTypeImage?.setImageResource(R.drawable.ic_antaran_co_design_icon)
            }

        }
        if(enquiryRes?.productHistoryId != null)
        {
            mBinding?.ProductName?.text = enquiryRes?.historyTag
            when(enquiryRes?.productHistoryStatus){
                0->{
                    mBinding?.typeProduct?.text = "Buyer Custom Product"
                    mBinding?.totalSteps?.text = "/10"
                    mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                }
                1 ->{
                    mBinding?.typeProduct?.text = "Made ro Order"
                    mBinding?.totalSteps?.text = "/10"
                    mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                }
                2->{
                    mBinding?.typeProduct?.text = "Available in Stock"
                    mBinding?.totalSteps?.text = "/7"
                    if(enquiryRes?.currenStageId!! > 4)
                    {
                        mBinding?.stepsCompleted?.text = (enquiryRes?.currenStageId!! - 3).toString()

                    }
                    else{
                        mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()
                    }

                }
            }

        }
        else{
            mBinding?.ProductName?.text = enquiryRes?.tag
            when(enquiryRes?.productStatus){
                0->{
                    mBinding?.typeProduct?.text = "Buyer Custom Product"
                    mBinding?.totalSteps?.text = "/10"
                    mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                }
                1 ->{
                    mBinding?.typeProduct?.text = "Made ro Order"
                    mBinding?.totalSteps?.text = "/10"
                    mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()

                }
                2->{
                    mBinding?.typeProduct?.text = "Available in Stock"
                    mBinding?.totalSteps?.text = "/7"
                    if(enquiryRes?.currenStageId!! > 4)
                    {
                        mBinding?.stepsCompleted?.text = (enquiryRes?.currenStageId!! - 3).toString()

                    }
                    else{
                        mBinding?.stepsCompleted?.text = enquiryRes?.currenStageId.toString()
                    }

                }
            }
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
        Log.d("moqcheck", "onCreate: "+moq)
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

        val moq = MoqsPredicates.getMoqs(enquiry)
        Log.d("moqcheck", "onCreate: "+moq)
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
    override fun onSendCustomMoqSuccess(moqId:Long) {
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

}