package com.adrosonic.craftexchangemarketing.ui.modules.pdfViewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityPdfViewerBinding
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs

fun Context.pdfViewerIntent(): Intent {
    return Intent(this, PdfViewerActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class PdfViewerActivity: AppCompatActivity() {

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
               if(viewType.equals("USER_MAN")){
                   mBinding?.webView?.settings?.javaScriptEnabled = true
                   pdf = ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV+"usermanual/AdminMobile.pdf"
                   mBinding?.webView?.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }
                Utility.displayMessage("Please wait while we are downloading the document",this)
           }

       }
   }
}

