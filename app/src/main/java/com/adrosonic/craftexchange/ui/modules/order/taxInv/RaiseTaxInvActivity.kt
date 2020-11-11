package com.adrosonic.craftexchange.ui.modules.order.taxInv

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.entities.realmEntities.TaxInvDetails
import com.adrosonic.craftexchange.database.predicates.TaxInvPredicates
import com.adrosonic.craftexchange.databinding.ActivityRaiseTaxInvBinding
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.google.gson.Gson
import java.io.File

fun Context.raiseTaxInvIntent(enquiryId:Long, isView:Boolean,tiRequest: SendTiRequest?): Intent {
    val intent = Intent(this, RaiseTaxInvActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("isView", isView)
    intent.putExtra("tiRequest", tiRequest)
    return intent
}

class RaiseTaxInvActivity : LocaleBaseActivity(),OrdersViewModel.tiInterface,OrdersViewModel.GenTaxInvInterface  {
    var enquiryId=0L
    var isView=false
    var tiDetails: TaxInvDetails? = null

    val mEnqVM : EnquiryViewModel by viewModels()
    val mOrdVM : OrdersViewModel by viewModels()

    var orderDetails: Orders? = null

    var loadingDialog : Dialog ?= null

    private var mBinding: ActivityRaiseTaxInvBinding? = null

    var ti=SendTiRequest()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRaiseTaxInvBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mOrdVM?.tiListener = this
        mOrdVM?.taxInvGenListener = this
        mBinding?.swipeTaxInvoice?.isEnabled = false

        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            isView=intent.getBooleanExtra("isView",false)
            orderDetails=mOrdVM.loadSingleOrderDetails(enquiryId,0)
//            ti=intent.getSerializableExtra("tiRequest") as SendTiRequest
            ti=intent.getSerializableExtra("tiRequest") as SendTiRequest
            enquiryId?.let{
                viewLoader()
                mOrdVM?.previewTi(enquiryId)
            }
        }

        loadingDialog = Utility?.loadingDialog(this)
        mBinding?.btnBack?.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        mBinding?.txtDownload?.setOnClickListener {
            val cacheFile = File(applicationContext.cacheDir, ConstantsDirectory.TI_PDF_PATH + "Ti${enquiryId}.pdf")
            if (cacheFile.exists()){
                Utility.openTaxInvFile(this,enquiryId)
            }
            else {
                if (Utility.checkIfInternetConnected(applicationContext)) {
                    viewLoader()
                    mOrdVM?.downloadTi(enquiryId)
                } else Utility.displayMessage(
                    getString(R.string.no_internet_connection),
                    applicationContext
                )
            }
        }

        setViews()

        mBinding?.btnRaiseTaxInv?.setOnClickListener {
            if(Utility.checkIfInternetConnected(this)){
                loadingDialog?.show()
//                viewLoader()
                mOrdVM?.generateTaxInvoice(ti)
            }else{
                Utility.displayMessage("Tax Invoice will be updated on Network Connection!",this)
                TaxInvPredicates.insertTiForOffline(enquiryId,1,ti)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    fun setViews(){
        mBinding?.enquiryCode?.text=getString(R.string.tax_invoice)+": ${orderDetails?.orderCode}"
        if(isView == true){
            mBinding?.btnRaiseTaxInv?.visibility = View.GONE
        }else{
            mBinding?.btnRaiseTaxInv?.visibility = View.VISIBLE
        }
        mBinding?.enquiryCode?.text="Tax invoice for ${orderDetails?.orderCode}"
        val webSettings = mBinding?.webviewTiPreview?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.builtInZoomControls = true
        if(Utility.checkIfInternetConnected(applicationContext)){
            mBinding?.webviewTiPreview?.loadDataWithBaseURL(null,getString(R.string.please_wait), "text/html", "utf-8", null)
        }else{
            mBinding?.webviewTiPreview?.loadDataWithBaseURL(null, getString(R.string.preview_not_available), "text/html", "utf-8", null)
        }
    }

    fun viewLoader(){
        mBinding?.pbLoader?.visibility= View.VISIBLE

    }
    fun hideLoader(){
        mBinding?.pbLoader?.visibility= View.GONE
    }

    override fun onTiDownloadSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                hideLoader()
                Utility.openTaxInvFile(this,enquiryId)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onTiDownloadFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                getString(R.string.unable_download_pdf)
                Utility.displayMessage( getString(R.string.unable_download_pdf),applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onTiHTMLSuccess(data: String) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                hideLoader()
                mBinding?.webviewTiPreview?.loadDataWithBaseURL(null, data, "text/html", "utf-8", null)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onTiHTMLFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                hideLoader()
                mBinding?.webviewTiPreview?.loadDataWithBaseURL(null, getString(R.string.preview_not_available), "text/html", "utf-8", null)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
        finish()
    }


    override fun onGenTaxInvSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                loadingDialog?.cancel()
                hideLoader()
                Utility.displayMessage("Tax Invoice Generated!",applicationContext)
                setResult(Activity.RESULT_OK)
                finish()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onGenTaxInvFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                loadingDialog?.cancel()
                hideLoader()
                Utility.displayMessage("Error generating Tax Invoice",applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("RaiseTaxInvActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
        if (requestCode == ConstantsDirectory.RESULT_TI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}