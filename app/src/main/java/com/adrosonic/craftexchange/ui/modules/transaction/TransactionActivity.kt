package com.adrosonic.craftexchange.ui.modules.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityArtisanProfileBinding
import com.adrosonic.craftexchange.databinding.ActivityTransactionBinding
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay.PaymentReceiptFragment
import com.adrosonic.craftexchange.ui.modules.artisan.profile.ArtisanProfileActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.transactionIntent(): Intent {
    return Intent(this, TransactionActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}

class TransactionActivity : AppCompatActivity() {

    private var mBinding : ActivityTransactionBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTransactionBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        when(Prefs.getString(ConstantsDirectory.PROFILE,"")){

            ConstantsDirectory.ARTISAN -> {}

            ConstantsDirectory.BUYER -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.transaction_container,
                            CommonTransacFragment.newInstance())
                        .commit()
                }
            }
        }
    }
}