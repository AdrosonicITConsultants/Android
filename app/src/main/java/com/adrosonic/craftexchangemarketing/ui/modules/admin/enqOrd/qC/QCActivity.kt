package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.qC

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityQCBinding
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.qcFormIntent(): Intent {
    return Intent(this, QCActivity::class.java).apply {}
}

class QCActivity : AppCompatActivity() {

    private var mBinding: ActivityQCBinding? = null

    var enqID : Long?= 0
    var status: Long?= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityQCBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        enqID = intent?.getLongExtra(ConstantsDirectory.ENQUIRY_ID,0)
        status = intent?.getLongExtra(ConstantsDirectory.ORDER_STATUS_FLAG,0)


                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.qc_container,
                            BuyerViewQcFormFragment.newInstance(enqID.toString(),status.toString()))
                        .commit()

                }

    }
}