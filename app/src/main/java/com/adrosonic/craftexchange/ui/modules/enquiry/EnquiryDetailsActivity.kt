package com.adrosonic.craftexchange.ui.modules.enquiry

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.replace
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityEnquiryDetailsBinding
import com.adrosonic.craftexchange.databinding.ActivitySearchSuggestionBinding
import com.adrosonic.craftexchange.ui.modules.artisan.auth.login.ArtisanLoginUsernameFragment
import com.adrosonic.craftexchange.ui.modules.buyer.auth.login.BuyerLoginUsernameFragment
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.adapter.BuyerOnGoEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.search.SearchSuggestionActivity
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
        var enqCode = intent?.getStringExtra(ConstantsDirectory.ENQUIRY_CODE)

        var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)

        when(profile){
            ConstantsDirectory.ARTISAN -> {

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