package com.adrosonic.craftexchange.ui.modules.pdfViewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityPdfViewerBinding
import com.adrosonic.craftexchange.ui.modules.artisan.landing.ArtisanLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs

fun Context.pdfViewerIntent(): Intent {
    return Intent(this, PdfViewerActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class PdfViewerActivity: LocaleBaseActivity() {

    private var mBinding : ActivityPdfViewerBinding?= null

    var pdf : String?= ""

   override fun onCreate(savedInstanceState: Bundle?){
       super.onCreate(savedInstanceState)
       mBinding = ActivityPdfViewerBinding.inflate(layoutInflater)
       val view = mBinding?.root
       setContentView(view)

       if(intent!=null){
           val viewType: String? = intent.getStringExtra("ViewType")
           if(!TextUtils.isEmpty(viewType) || viewType!=null){
               if(viewType.equals("Terms_conditions")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
                   pdf =ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"documents/TERMS_and_CONDITIONS.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }

               if(viewType.equals("PRIVACY_POLICY_PDF")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"documents/PRIVACY%20POLICY.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }

               if(viewType.equals("LEGAL_DISCLAIMER")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"documents/LEGAL%20DISCLAIMER.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }
               if(viewType.equals("HELP")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"documents/Help.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }
               if(viewType.equals("FAQ_PDF")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
//                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"documents/LEGAL%20DISCLAIMER.pdf"
                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"documents/FAQ.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }
               if(viewType.equals("USER_MAN_ART")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"usermanual/ArtisanMobile.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }
               if(viewType.equals("USER_MAN_BUY")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"usermanual/BuyerMobile.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }
                Utility.displayMessage(getString(R.string.plz_Wait),this)
           }

       }
   }
}

