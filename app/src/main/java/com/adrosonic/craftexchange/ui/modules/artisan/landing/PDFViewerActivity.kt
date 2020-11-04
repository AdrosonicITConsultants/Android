package com.adrosonic.craftexchange.ui.modules.artisan.landing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityFaqViewerBinding
import kotlinx.android.synthetic.main.activity_faq_viewer.*


fun Context.PDFViewerActivity(): Intent {
    return Intent(this, PDFViewerActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}

class PDFViewerActivity: AppCompatActivity() {

    private var mBinding : ActivityFaqViewerBinding?= null

   override fun onCreate(savedInstanceState: Bundle?){
       super.onCreate(savedInstanceState)

       setContentView(R.layout.activity_faq_viewer)


       val webview = findViewById<View>(R.id.web_view) as WebView

       if(intent!=null){
           val viewType: String? = intent.getStringExtra("ViewType")
           if(!TextUtils.isEmpty(viewType) || viewType!=null){
               if(viewType.equals("Terms_conditions")){
                   webview.settings.javaScriptEnabled = true
                   val pdf =
                       "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/documents/TERMS_and_CONDITIONS.pdf"
                   webview.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }

               if(viewType.equals("PRIVACY_POLICY_PDF")){
                   webview.settings.javaScriptEnabled = true
                   val pdf =
                       "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/documents/PRIVACY%20POLICY.pdf"
                   webview.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")

               }

               if(viewType.equals("LEGAL_DISCLAIMER")){
                   webview.settings.javaScriptEnabled = true
                   val pdf =
                       "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/documents/LEGAL%20DISCLAIMER.pdf"
                   webview.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
               }

           }
       }


   }
}

