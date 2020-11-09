package com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityEnquiryPaymentBinding
import com.adrosonic.craftexchange.enums.EnquiryStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.CompPaymentReceiptFragment
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.PaymentReceiptFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.enquiryPayment(): Intent {
    return Intent(this, EnquiryPaymentActivity::class.java).apply {}
}

const val ENQ_SCREEN = 75
class EnquiryPaymentActivity : LocaleBaseActivity() {

    private var mBinding: ActivityEnquiryPaymentBinding? = null

    var enqID : Long?= 0
    var piID : Long?= 0
    var enqStatus : Long ?= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEnquiryPaymentBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        enqID = intent?.getLongExtra(ConstantsDirectory.ENQUIRY_ID,0)
        piID = intent?.getLongExtra(ConstantsDirectory.PI_ID,0)
        enqStatus = intent?.getLongExtra(ConstantsDirectory.ENQUIRY_STATUS_FLAG,0)

        when(Prefs.getString(ConstantsDirectory.PROFILE,"")){
            ConstantsDirectory.ARTISAN -> {
                when(enqStatus){
                    EnquiryStatus.COMPLETED.getId() ->{
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.enquiry_payment_container,
                                    CompPaymentReceiptFragment.newInstance(enqID.toString()))
                                .commit()
                        }
                    }
                    EnquiryStatus.ONGOING.getId() -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.enquiry_payment_container,
                                    PaymentReceiptFragment.newInstance(enqID.toString()))
                                .commit()
                        }
                    }
                }
            }

            ConstantsDirectory.BUYER -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.enquiry_payment_container,
                            AdvPay1Fragment.newInstance(enqID.toString(),piID.toString()))
                        .commit()
                }
            }
        }


    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        var bundle = Bundle()
//        Prefs.putString(ConstantsDirectory.ENQUIRY_ID, enqID.toString()) //TODO change later
//        bundle.putString(ConstantsDirectory.ENQUIRY_ID, enqID.toString())
//        bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG, "2")
//
//        startActivity(this.enquiryDetails().putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//    }
}