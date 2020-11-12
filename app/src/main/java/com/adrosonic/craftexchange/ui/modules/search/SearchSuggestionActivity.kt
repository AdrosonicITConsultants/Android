package com.adrosonic.craftexchange.ui.modules.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivitySearchSuggestionBinding
import com.adrosonic.craftexchange.ui.modules.artisan.search.ArtisanSuggestionFragment
import com.adrosonic.craftexchange.ui.modules.buyer.search.BuyerSuggestionFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.searchSuggestionIntent(): Intent {
    return Intent(this, SearchSuggestionActivity::class.java).apply {}
}

class SearchSuggestionActivity : LocaleBaseActivity() {

    private var mBinding: ActivitySearchSuggestionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchSuggestionBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)

        when(profile){
            ConstantsDirectory.ARTISAN -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.ss_container,
                            ArtisanSuggestionFragment.newInstance(),"ArtisanSuggestionList")
                        .commit()
                }
            }
            ConstantsDirectory.BUYER -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.ss_container,
                            BuyerSuggestionFragment.newInstance(-1L),"BuyerSuggestionList") // -1 for all products
                        .commit()
                }
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}