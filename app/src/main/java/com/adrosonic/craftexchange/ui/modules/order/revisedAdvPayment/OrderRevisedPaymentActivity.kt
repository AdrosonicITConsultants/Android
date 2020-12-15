package com.adrosonic.craftexchange.ui.modules.order.revisedAdvPayment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityEnquiryPaymentBinding
import com.adrosonic.craftexchange.databinding.ActivityOrderRevisedPaymentBinding
import com.adrosonic.craftexchange.enums.EnquiryStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.CompPaymentReceiptFragment
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.PaymentReceiptFragment
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay.AdvPay1Fragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.orderPayment(): Intent {
    return Intent(this, OrderRevisedPaymentActivity::class.java).apply {}
}

const val ENQ_SCREEN = 75
class OrderRevisedPaymentActivity : LocaleBaseActivity() {

    private var mBinding: ActivityOrderRevisedPaymentBinding? = null

    var enqID : Long?= 0
    var piID : Long?= 0
    var enqStatus : Long ?= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOrderRevisedPaymentBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        enqID = intent?.getLongExtra(ConstantsDirectory.ENQUIRY_ID,0)
        piID = intent?.getLongExtra(ConstantsDirectory.PI_ID,0)
        enqStatus = intent?.getLongExtra(ConstantsDirectory.ORDER_STATUS_FLAG,0)

        when(Prefs.getString(ConstantsDirectory.PROFILE,"")){
            ConstantsDirectory.ARTISAN -> {
                when(enqStatus){
                    1L ->{
                        if (savedInstanceState == null) {
//                            supportFragmentManager.beginTransaction()
//                                .replace(R.id.order_payment_container,
//                                    CompPaymentReceiptFragment.newInstance(enqID.toString()))
//                                .commit()
                        }
                    }
                    0L -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.order_payment_container,
                                    RevisedPaymentReceiptFragment.newInstance(enqID.toString()))
                                .commit()
                        }
                    }
                }
            }

            ConstantsDirectory.BUYER -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.order_payment_container,
                            SelectRevisedAdvPayFragment.newInstance(enqID?:0,piID?:0))
                        .commit()
                }
            }
        }


    }
}