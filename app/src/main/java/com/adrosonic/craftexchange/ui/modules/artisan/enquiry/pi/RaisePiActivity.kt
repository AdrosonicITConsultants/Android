package com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi

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
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.databinding.ActivityRaisePiBinding
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import java.io.File

fun Context.raisePiContext(enquiryId:Long,isView:Boolean,piRequest: SendPiRequest?): Intent {
    val intent = Intent(this, RaisePiActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("isView", isView)
    intent.putExtra("piRequest", piRequest)
    return intent
}

class RaisePiActivity : AppCompatActivity(),
EnquiryViewModel.piInterface{
    var enquiryId=0L
    var isView=false
    var piDetails: PiDetails? = null
    val mEnqVM : EnquiryViewModel by viewModels()
    var enquiryDetails: OngoingEnquiries? = null
    private var mBinding: ActivityRaisePiBinding? = null
    var pi=SendPiRequest()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRaisePiBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mEnqVM?.piLisener=this
        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            isView=intent.getBooleanExtra("isView",false)
            enquiryDetails=mEnqVM?.loadSingleEnqDetails(enquiryId)
//            piDetails=PiPredicates.getSinglePi(enquiryId)
            pi=intent.getSerializableExtra("piRequest") as SendPiRequest
            enquiryId?.let{
                viewLoader()
                mEnqVM?.previewPi(enquiryId)
            }
        }
        mBinding?.btnBack?.setOnClickListener {
            finish()
        }
        mBinding?.txtDownload?.setOnClickListener {
            val cacheFile = File(applicationContext.cacheDir, ConstantsDirectory.PI_PDF_PATH + "Pi${enquiryId}.pdf")
            if (cacheFile.exists()){
             Utility.openFile(applicationContext,enquiryId)
            }
            else {
                if (Utility.checkIfInternetConnected(applicationContext)) {
                    viewLoader()
                    mEnqVM?.downloadPi(enquiryId)
                } else Utility.displayMessage(
                    getString(R.string.no_internet_connection),
                    applicationContext
                )
            }
        }
        mBinding?.btnRaisePi?.setOnClickListener {
            if (Utility.checkIfInternetConnected(applicationContext)) {
                viewLoader()
                mEnqVM?.sendPi(enquiryId,pi)
            } else {
                PiPredicates.insertPiForOffline(enquiryId,1,1,pi)
                Utility.displayMessage("Pi will be send once internet connectivity regained.",applicationContext)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        setViews()
    }

    fun setViews(){
        if(isView){
            mBinding?.btnRaisePi?.visibility=View.GONE
        }
        else{
            mBinding?.btnRaisePi?.visibility=View.VISIBLE
        }
        mBinding?.enquiryCode?.text="Proforma invoice for ${enquiryDetails?.enquiryCode}"
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
                Utility.openFile(applicationContext,enquiryId)
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
                mBinding?.webviewPiPreview?.loadDataWithBaseURL(null, getString(R.string.preview_not_available), "text/html", "utf-8", null)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }
}