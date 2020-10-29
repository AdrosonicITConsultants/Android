package com.adrosonic.craftexchange.ui.modules.artisan.landing

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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



//       mBinding?.FAQPDFView?.fromAsset( "FAQ_Dummy.pdf")
//           ?.password(null)
//          ?.defaultPage(0)
//           ?.enableSwipe(true)
//           ?.swipeHorizontal(false)
//           ?.enableDoubletap(true)
//           ?.load()

       if(intent!=null){
           val viewType: String? = intent.getStringExtra("ViewType")

           if(!TextUtils.isEmpty(viewType) || viewType!=null){
               if(viewType.equals("assets")){
                   FAQ_PDF_view.fromAsset( "FAQ_Dummy.pdf")
                       .password(null)
                       .defaultPage(0)
                       .enableSwipe(true)
                       .swipeHorizontal(false)
                       .enableDoubletap(true)
                       .load()
               }
           }
       }


   }
}

