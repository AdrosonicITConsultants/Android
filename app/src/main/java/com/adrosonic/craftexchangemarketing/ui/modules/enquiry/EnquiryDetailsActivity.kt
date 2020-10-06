package com.adrosonic.craftexchangemarketing.ui.modules.enquiry

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityEnquiryDetailsBinding
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.enquiry.ArtisanOnGoEnqDetailsFragment
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.auth.login.BuyerLoginUsernameFragment
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.enquiry.adapter.BuyerOnGoEnqDetailsFragment
import com.adrosonic.craftexchangemarketing.ui.modules.search.SearchSuggestionActivity
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
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

        when(enqStatus){
            //Closed
            1L -> {
                if (savedInstanceState == null) {
                    enqID?.let { CompEnqDetailsFragment.newInstance(it, enqStatus.toString(),isArtsan) }?.let {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.enquiry_details_container, it)
                            .commitNow()
                    }
                }
            }
            //Ongoing
            2L -> {
                when(profile){
                    ConstantsDirectory.ARTISAN -> {
                        if (savedInstanceState == null) {
                            enqID?.let { ArtisanOnGoEnqDetailsFragment.newInstance(it) }?.let {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.enquiry_details_container, it)
                                    .commitNow()
                            }
                        }
                    }
                    ConstantsDirectory.BUYER -> {
                        if (savedInstanceState == null) {
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