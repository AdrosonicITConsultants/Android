package com.adrosonic.craftexchange.ui.modules.buyer.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.databinding.ActivityBuyerSearchResultsBinding
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.SearchViewModel

fun Context.searchBuyerIntent(): Intent {
    return Intent(this, BuyerSearchActivity::class.java)
}

class BuyerSearchActivity : AppCompatActivity() {

    private var mBinding : ActivityBuyerSearchResultsBinding?= null
    val mViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBuyerSearchResultsBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        // Verify the action and get the query
        intent?.let { handleIntent(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

     private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                Utility?.displayMessage("u searched : $query",applicationContext)
                var data = mViewModel?.getBuyerSearchData(query)
                Log.e("BuySearch",data.toString())
            }
        }
    }

}