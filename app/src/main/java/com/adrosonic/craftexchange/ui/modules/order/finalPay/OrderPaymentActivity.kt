package com.adrosonic.craftexchange.ui.modules.order.finalPay

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityEnquiryPaymentBinding
import com.adrosonic.craftexchange.databinding.ActivityOrderPaymentBinding
import com.adrosonic.craftexchange.enums.EnquiryStatus
import com.adrosonic.craftexchange.enums.OrderStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.CompPaymentReceiptFragment
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.PaymentReceiptFragment
import com.adrosonic.craftexchange.ui.modules.artisan.order.FinalPayReceiptFragment
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay.EnquiryPaymentActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.orderPaymentIntent(): Intent {
    return Intent(this, OrderPaymentActivity::class.java).apply {  }
}

class OrderPaymentActivity : AppCompatActivity() {

    private var mBinding: ActivityOrderPaymentBinding? = null

    var enqID : Long?= 0
    var orderStatus : Long ?= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOrderPaymentBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        enqID = intent?.getLongExtra(ConstantsDirectory.ENQUIRY_ID,0)
//        piID = intent?.getLongExtra(ConstantsDirectory.PI_ID,0)
        orderStatus = intent?.getLongExtra(ConstantsDirectory.ENQUIRY_STATUS_FLAG,0)

        when(Prefs.getString(ConstantsDirectory.PROFILE,"")){
            ConstantsDirectory.ARTISAN -> {
                when(orderStatus){
                    OrderStatus.COMPLETED.getId() ->{
//                        if (savedInstanceState == null) {
//                            supportFragmentManager.beginTransaction()
//                                .replace(R.id.order_payment_container,
//                                    CompPaymentReceiptFragment.newInstance(""))
//                                .commit()
//                        }
                    }
                    OrderStatus.ONGOING.getId() -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.order_payment_container,
                                    FinalPayReceiptFragment.newInstance(enqID.toString(),""))
                                .commit()
                        }
                    }
                }
            }

            ConstantsDirectory.BUYER -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.order_payment_container,
                            FinPay1Fragment.newInstance(enqID.toString(),""))
                        .commit()
                }
            }
        }

    }
}