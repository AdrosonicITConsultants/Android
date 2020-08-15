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
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ArtisanProductsViewModel
import com.adrosonic.craftexchange.viewModels.SearchViewModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_wishlist.*

fun Context.searchArtisanIntent(): Intent {
    return Intent(this, ArtisanSearchActivity::class.java)
}

class ArtisanSearchActivity : AppCompatActivity(),
    ArtisanProductsViewModel.productsFetchInterface {

    private var mBinding : ActivityArtisanSearchBinding?= null
    val mViewModel: SearchViewModel by viewModels()
    var adapter : ArtisanSearchAdapter?= null
    var searchFilter : String ?= ""
    var filteredProducts : MutableLiveData<RealmResults<ArtisanProducts>> ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanSearchBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        intent?.let { handleIntent(it) }

        mBinding?.searchArtisan?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query:String):Boolean {
                setupRecyclerView(query)
                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
                // adapter.getFilter().filter(newText);
                return false
            }
        })

//        searchFilter?.let {
//            mViewModel?.getArtisanSearchData(it)?.observe(this,
//                Observer<RealmResults<ArtisanProducts>> {
//                    adapter?.updateProductList(it)
//                })
//        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                Utility?.displayMessage("u searched : $query",applicationContext)
                searchFilter = query
                setupRecyclerView(searchFilter!!)

                var data = mViewModel?.getArtisanSearchData(query)
                Log.e("ArtSearch",data.toString())
            }
        }
    }

    private fun setupRecyclerView(filter : String){
        mBinding?.artisanSearchList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ArtisanSearchAdapter(this, filter?.let { mViewModel.getArtisanSearchData(it).value })
        mBinding?.artisanSearchList?.adapter = adapter
        mBinding?.listSizeText?.text =
            "Found ${adapter?.itemCount} items"
        adapter?.notifyDataSetChanged()

    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFailure() {
        TODO("Not yet implemented")
    }
}