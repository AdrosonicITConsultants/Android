package com.adrosonic.craftexchange.ui.modules.enquiry

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityEnquiryDetailsBinding
import com.adrosonic.craftexchange.enums.EnquiryStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.ArtisanOnGoEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.adapter.BuyerOnGoEnqDetailsFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.enquiryDetails(): Intent {
    return Intent(this, EnquiryDetailsActivity::class.java).apply {}
}
class EnquiryDetailsActivity : AppCompatActivity() {

    private var mBinding: ActivityEnquiryDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEnquiryDetailsBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var enqID = intent?.getStringExtra(ConstantsDirectory.ENQUIRY_ID)
        var enqStatus = intent?.getStringExtra(ConstantsDirectory.ENQUIRY_STATUS_FLAG)?.toLong()
        var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)
        val isArtsan= when(profile) {
            ConstantsDirectory.ARTISAN -> true
            else-> false
        }
        Log.e("ViewEnquiry","22222222222222: $profile")
        when(enqStatus){
            //Closed
            EnquiryStatus.COMPLETED.getId() -> {
                if (savedInstanceState == null) {
                    enqID?.let { CompEnqDetailsFragment.newInstance(it, enqStatus.toString(),isArtsan) }?.let {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.enquiry_details_container, it)
                            .commitNow()
                    }
                }
            }
            //Ongoing
            EnquiryStatus.ONGOING.getId() -> {
                when(profile){
                    ConstantsDirectory.ARTISAN -> {
                        if (savedInstanceState == null) {
                            Log.e("ViewEnquiry","333333333")
                            enqID?.let { ArtisanOnGoEnqDetailsFragment.newInstance(it) }?.let {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.enquiry_details_container, it)
                                    .commitNow()
                            }
                        }
                    }
                    ConstantsDirectory.BUYER -> {
                        if (savedInstanceState == null) {
                            Log.e("ViewEnquiry","44444444444")
                            enqID?.let { BuyerOnGoEnqDetailsFragment.newInstance(it) }?.let {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.enquiry_details_container, it)
                                    .commitNow()
                            }
                        }
                    }
                }
            }
        }

    }
}