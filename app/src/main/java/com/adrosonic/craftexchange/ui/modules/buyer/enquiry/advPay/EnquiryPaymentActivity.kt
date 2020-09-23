package com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.replace
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityEnquiryDetailsBinding
import com.adrosonic.craftexchange.databinding.ActivityEnquiryPaymentBinding
import com.adrosonic.craftexchange.ui.modules.role.RoleSelectFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory

fun Context.enquiryPayment(): Intent {
    return Intent(this, EnquiryPaymentActivity::class.java).apply {}
}

class EnquiryPaymentActivity : AppCompatActivity() {

    private var mBinding: ActivityEnquiryPaymentBinding? = null

    var enqID : Long?= 0
    var piID : Long?= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEnquiryPaymentBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        enqID = intent?.getLongExtra(ConstantsDirectory.ENQUIRY_ID,0)
        piID = intent?.getLongExtra("PIID",0)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.enquiry_payment_container,
                    AdvPay1Fragment.newInstance(enqID.toString(),piID.toString()))
//                ?.addToBackStack(null)
                .commit()
        }
    }
}