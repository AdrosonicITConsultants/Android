package com.adrosonic.craftexchange.ui.modules.artisan.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.databinding.ActivityArtisanSearchBinding
import com.adrosonic.craftexchange.databinding.ActivityBuyerSearchResultsBinding
import com.adrosonic.craftexchange.ui.modules.artisan.products.UploadedProductsListAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.search.BuyerSearchActivity
import com.adrosonic.craftexchange.ui.modules.role.RoleSelectFragment
import com.adrosonic.craftexchange.ui.modules.search.SuggestionFragment
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ArtisanProductsViewModel
import com.adrosonic.craftexchange.viewModels.SearchViewModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_wishlist.*

fun Context.searchArtisanIntent(): Intent {
    return Intent(this, ArtisanSearchActivity::class.java).apply {}
}

class ArtisanSearchActivity : AppCompatActivity() {

    private var mBinding: ActivityArtisanSearchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanSearchBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.ss_container,
                    SuggestionFragment.newInstance(),"SuggestionList")
                .commit()
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}