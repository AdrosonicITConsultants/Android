package com.adrosonic.craftexchangemarketing.ui.modules.artisan.enquiry.pi

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
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchangemarketing.database.predicates.MoqsPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.PiPredicates
import com.adrosonic.craftexchangemarketing.databinding.ActivityRaisePiBinding
import com.adrosonic.craftexchangemarketing.repository.data.request.pi.SendPiRequest
//import com.adrosonic.craftexchangemarketing.ui.modules.order.revisePi.viewOldPiContext
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryViewModel
import java.io.File

fun Context.raisePiContext(enquiryId:Long,isView:Boolean,piRequest: SendPiRequest?,isCompleted:Boolean): Intent {
    val intent = Intent(this, RaisePiActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("isView", isView)
    intent.putExtra("piRequest", piRequest)
    intent.putExtra("isCompleted", isCompleted)
    return intent
}

class RaisePiActivity : AppCompatActivity(),
    EnquiryViewModel.piInterface{
    var enquiryId=0L
    var isView=false
    var isCompleted=false
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
            isCompleted=intent.getBooleanExtra("isCompleted",false)
//            enquiryDetails=mEnqVM?.loadSingleEnqDetails(enquiryId)
            pi=intent.getSerializableExtra("piRequest") as SendPiRequest
            enquiryId?.let{
                viewLoader()
                mEnqVM?.previewPi(enquiryId,"false")
            }
            Log.e("PINull","enquiryId: $enquiryId")
        }

        Log.e("PiPostCr","enquiryId: $enquiryId")
        mBinding?.btnBack?.setOnClickListener {
            finish()
        }

        setViews()
    }

    fun setViews(){
       mBinding?.enquiryCode?.text=getString(R.string.proforma_invoice)
        try{ if(isCompleted)mBinding?.enquiryCode?.text=getString(R.string.proforma_invoice)} catch (e:Exception){}
        val webSettings = mBinding?.webviewPiPreview?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.builtInZoomControls = true
        if(Utility.checkIfInternetConnected(applicationContext)){
            mBinding?.webviewPiPreview?.loadDataWithBaseURL(null,"PleaseWait", "text/html", "utf-8", null)
        }else{
            mBinding?.webviewPiPreview?.loadDataWithBaseURL(null, getString(R.string.preview_not_available), "text/html", "utf-8", null)
        }
    }
    fun viewLoader(){
//        mBinding?.pbLoader?.visibility=View.VISIBLE

    }
    fun hideLoader(){
//        mBinding?.pbLoader?.visibility=View.GONE
    }
    override fun onPiFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
//                mBinding?.btnRaisePi?.setText("Swipe to raise PI")
//                mBinding?.btnRaisePi?.isEnabled=true
//                Utility.displayMessage(getString(R.string.unable_raise_pi),applicationContext)
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
//        try {
//            Handler(Looper.getMainLooper()).post(Runnable {
//                Log.e("Enquiry Details", "onSuccess")
//                hideLoader()
//                Utility.openFile(this,enquiryId,"")
//            })
//        } catch (e: Exception) {
//            Log.e("Enquiry Details", "Exception onFailure " + e.message)
//        }
    }

  override fun onPiDownloadFailure() {
//        try {
//            Handler(Looper.getMainLooper()).post(Runnable {
//                hideLoader()
//                Utility.displayMessage( getString(R.string.unable_download_pdf),applicationContext)
//            })
//        } catch (e: Exception) {
//            Log.e("Enquiry Details", "Exception onFailure " + e.message)
//        }
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
//            Handler(Looper.getMainLooper()).post(Runnable {
//                Log.e("Enquiry Details", "onSuccess")
//                hideLoader()
//                enquiryDetails=mEnqVM?.loadSingleEnqDetails(enquiryId)
//                if(enquiryDetails?.isPiSend==1L)mBinding?.piText?.text=getString(R.string.view_old_pi)
//                mBinding?.webviewPiPreview?.loadDataWithBaseURL(null, getString(R.string.preview_not_available), "text/html", "utf-8", null)
//            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("RaisePiActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()

            }
        }
    }
}