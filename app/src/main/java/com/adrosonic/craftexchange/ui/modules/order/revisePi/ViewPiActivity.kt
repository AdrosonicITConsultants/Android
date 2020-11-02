package com.adrosonic.craftexchange.ui.modules.order.revisePi

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.databinding.ActivityRaisePiBinding
import com.adrosonic.craftexchange.databinding.ActivityViewPiBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import java.io.File


fun Context.viewPiContextPostCr(enquiryId:Long): Intent {
    val intent = Intent(this, ViewPiActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent
}
class ViewPiActivity : AppCompatActivity(),
OrdersViewModel.FetchOrderInterface,
EnquiryViewModel.FetchEnquiryInterface,
EnquiryViewModel.piInterface{
    var enquiryId=0L
    var isRevisedPi=false
    val mEnqVM : EnquiryViewModel by viewModels()
    val mOrderVm : OrdersViewModel by viewModels()
    var enquiryDetails: OngoingEnquiries? = null
    private var mBinding: ActivityViewPiBinding? = null
    private var orderDetails : Orders?= null
    var isPiSend=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityViewPiBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mEnqVM?.piLisener=this
        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            enquiryId?.let {
                viewLoader()
                enquiryDetails=mEnqVM?.loadSingleEnqDetails(enquiryId)
                if(enquiryDetails==null)mEnqVM?.getSingleOngoingEnquiry(enquiryId)
                orderDetails= OrdersPredicates.getSingleOnGoOrderDetails(it,0)
                mEnqVM?.getOldPiData(it)
                mEnqVM?.previewPi(enquiryId,"false")
            }
        }

        mBinding?.btnBack?.setOnClickListener {
            finish()
        }
        mBinding?.txtDownload?.setOnClickListener {
            val cacheFile = File(applicationContext.cacheDir, ConstantsDirectory.PI_PDF_PATH + "Pi${enquiryId}.pdf")
            if (cacheFile.exists()){
             Utility.openFile(this,enquiryId,"")
            }
            else {
                if (Utility.checkIfInternetConnected(applicationContext)) {
                    viewLoader()
                    mEnqVM?.downloadPi(enquiryId,"false")
                } else Utility.displayMessage(
                    getString(R.string.no_internet_connection),
                    applicationContext
                )
            }
        }
        mBinding?.btnRevisePi?.setOnClickListener {
         Log.e("PiPostCr","btnRaisePi isRevisedPi: $isRevisedPi")
         enquiryId?.let {startActivityForResult(this.revisePiContext(it),ConstantsDirectory.RESULT_PI)}
        }
        mBinding?.viewOldPi?.setOnClickListener {
            startActivity(this.viewOldPiContext(enquiryId))

        }
        setViews()
    }

    fun setViews(){
        Log.e("PiPostCr","enquiryId: ${orderDetails?.enquiryId}")
        if(isRevised())mBinding?.btnRevisePi?.visibility=View.VISIBLE
        else mBinding?.btnRevisePi?.visibility=View.GONE

        if(orderDetails?.isPiSend!!.equals(1L))mBinding?.viewOldPi?.visibility=View.VISIBLE
        else mBinding?.viewOldPi?.visibility=View.GONE

        mBinding?.enquiryCode?.text="Proforma invoice for ${orderDetails?.orderCode}"
        val webSettings = mBinding?.webviewPiPreview?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.builtInZoomControls = true
        if(Utility.checkIfInternetConnected(applicationContext)){
            mBinding?.webviewPiPreview?.loadDataWithBaseURL(null,"Please wait...", "text/html", "utf-8", null)
        }else{
            mBinding?.webviewPiPreview?.loadDataWithBaseURL(null, getString(R.string.preview_not_available), "text/html", "utf-8", null)
        }

    }
    fun viewLoader(){
        mBinding?.pbLoader?.visibility=View.VISIBLE

    }
    fun hideLoader(){
        mBinding?.pbLoader?.visibility=View.GONE
    }
    override fun onPiFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                Utility.displayMessage("Unable to raise PI, please try after some time",applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onPiSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                hideLoader()
                setResult(Activity.RESULT_OK)
                finish()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onPiDownloadSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                hideLoader()
                Utility.openFile(this,enquiryId,"")
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onPiDownloadFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                Utility.displayMessage("Unable to download PDF, pleas try again later",applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onPiHTMLSuccess( data:String) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                hideLoader()
                mBinding?.webviewPiPreview?.loadDataWithBaseURL(null, data, "text/html", "utf-8", null)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onPiHTMLFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                hideLoader()
                orderDetails=OrdersPredicates.getSingleOnGoOrderDetails(enquiryId,0)
                setViews()
                mBinding?.webviewPiPreview?.loadDataWithBaseURL(null, getString(R.string.preview_not_available), "text/html", "utf-8", null)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    fun isRevised():Boolean{
        Log.e("PiPostCr","isRevisedPi isPiSend: ${orderDetails?.isPiSend}")
        try {
            if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
                if(orderDetails?.changeRequestOn!!>0){
                if(orderDetails?.enquiryStageId!!>=4L && (orderDetails?.changeRequestStatus!!.equals(1L)||orderDetails?.changeRequestStatus!!.equals(3L))) {
                    if(orderDetails?.isPiSend!!.equals(0L)) {
                        Log.e("PiPostCr","isRevisedPi : true")
    //                    var days = Utility.getDateDiffInDays(Utility.returnDisplayDate( orderDetails?.changeRequestModifiedOn ?: ""))
    //                    Log.e("PiPostCr", "isRevisedPi days: $days")
    //                    if (days <= 4) {
                        return true
    //                    } else return false
                    } else return false
                } else return false

                } else return false
            }else return false
        } catch (e: Exception) {
            return false
        }
    }

    override fun onFailure() {

    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("EnquiryDetails", "onSuccess")
                enquiryDetails=mEnqVM.loadSingleEnqDetails(enquiryId)
                orderDetails= OrdersPredicates.getSingleOnGoOrderDetails(enquiryId,0)
                setViews()
                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("EnquiryDetails", "Exception onFailure " + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("ViewPiActivity", "RESULT_OK ${Activity.RESULT_OK}")
        Log.e("ViewPiActivity", "resultCode ${resultCode}")
        Log.e("ViewPiActivity", "requestCode ${requestCode}")
        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                viewLoader()
                mEnqVM?.previewPi(enquiryId, "false")
                mOrderVm?.getSingleOngoingOrder(enquiryId)
            }
        }
        orderDetails= OrdersPredicates.getSingleOnGoOrderDetails(enquiryId,0)
        orderDetails?.let{setViews()}
    }
}