package com.adrosonic.craftexchange.ui.modules.artisan.deliveryReceipt

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.CompPaymentReceiptFragment
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.PiActivity

fun Context.uploadDeliveryReceiptIntent(enquiryId:Long,orderStatus:Long): Intent {
    val intent = Intent(this, UploadDeliveryReceiptActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("orderStatus",orderStatus)
    return intent
}
class UploadDeliveryReceiptActivity : AppCompatActivity() {

    var enquiryId=0L
    var orderStatus = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_delivery_receipt)

        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            orderStatus=intent.getLongExtra("orderStatus",0)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.delivery_receipt_activity,
                    UploadDelRec1Fragment.newInstance(enquiryId.toString(),orderStatus.toString()))
                .commit()
        }

    }
}