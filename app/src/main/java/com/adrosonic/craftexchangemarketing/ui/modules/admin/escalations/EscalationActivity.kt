package com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchangemarketing.databinding.ActivityEscalationBinding
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndEnquiryDetailsBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.EnquiryDetailsActivity
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter.EscalationAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanProfileAdapter


fun Context.escalationIntent(total: Long): Intent {
    val intent = Intent(this, EnquiryDetailsActivity::class.java)
    intent.putExtra("total", total)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}
class EscalationActivity :AppCompatActivity(){
    private var mBinding : ActivityEscalationBinding?= null
    var total : Long?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityEscalationBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)


        if (intent.extras != null) {
            total = intent.getLongExtra("total", 1)
            Log.d("type", "onCreate: " + total)

        }
        mBinding?.backButton?.setOnClickListener {
            this.onBackPressed()
        }
        mBinding?.escalationCount?.text = total.toString()

        mBinding?.EscalationsViewPager?.adapter = EscalationAdapter(this,supportFragmentManager)
        mBinding?.EscalationsTab?.setupWithViewPager(mBinding?.EscalationsViewPager)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}