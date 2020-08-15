package com.adrosonic.craftexchange.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.SearchPredicates
import io.realm.RealmResults

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    val searchBProducts : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }
    val searchAProducts : MutableLiveData<RealmResults<ArtisanProducts>> by lazy { MutableLiveData<RealmResults<ArtisanProducts>>() }



    fun getBuyerSearchData(searchFilter : String): MutableLiveData<RealmResults<ProductCatalogue>> {
        searchBProducts.value=loadBSearchResults(searchFilter)
        return searchBProducts
    }

    private fun loadBSearchResults(searchFilter : String): RealmResults<ProductCatalogue>? {
        return SearchPredicates?.buyerSearch(searchFilter)
    }

    fun getArtisanSearchData(searchFilter : String): MutableLiveData<RealmResults<ArtisanProducts>> {
        searchAProducts.value=loadASearchResults(searchFilter)
        return searchAProducts
    }

    private fun loadASearchResults(searchFilter : String): RealmResults<ArtisanProducts>? {
        return SearchPredicates?.artisanSearch(searchFilter)
    }



}