package com.adrosonic.craftexchange.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.SearchPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.search.SuggestionResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    interface FetchArtisanSuggestions{
        fun onSuccessSugg(sug : SuggestionResponse)
        fun onFailureSugg()
    }

    interface FetchBuyerSuggestions{
        fun onSuccessSugg(sug : SuggestionResponse)
        fun onFailureSugg()
    }

    var artSugListener: FetchArtisanSuggestions? = null
    var buySugListener: FetchBuyerSuggestions? = null

    val searchBProducts : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }
    val searchAProducts : MutableLiveData<RealmResults<ArtisanProducts>> by lazy { MutableLiveData<RealmResults<ArtisanProducts>>() }


    fun getBuyerSearchData(searchFilter : String): MutableLiveData<RealmResults<ProductCatalogue>> {
        searchBProducts.value=loadBSearchResults(searchFilter)
        return searchBProducts
    }

    private fun loadBSearchResults(searchFilter : String): RealmResults<ProductCatalogue>? {
        return SearchPredicates.buyerSearch(searchFilter)
    }

    fun getArtisanSearchData(searchFilter : String, isMadeWithAntaran : Long): MutableLiveData<RealmResults<ArtisanProducts>> {
        searchAProducts.value=loadASearchResults(searchFilter,isMadeWithAntaran)
        return searchAProducts
    }

    private fun loadASearchResults(searchFilter : String,isMadeWithAntaran : Long): RealmResults<ArtisanProducts>? {
        return SearchPredicates.artisanSearch(searchFilter,isMadeWithAntaran)
    }

    fun getArtisanSearchSuggestions(str : String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        CraftExchangeRepository
            .getSearchService()
            .getArtisanSuggestions(token,str).enqueue(object : Callback, retrofit2.Callback<SuggestionResponse> {
                override fun onFailure(call: Call<SuggestionResponse>, t: Throwable) {
                    t.printStackTrace()
                    artSugListener?.onFailureSugg()
                }

                override fun onResponse(
                    call: Call<SuggestionResponse>,
                    response: Response<SuggestionResponse>
                ) {
                    if(response.body()?.valid == true){
                        artSugListener?.onSuccessSugg(response.body()!!)
                    }else{
                        artSugListener?.onFailureSugg()
                    }
                }
            })
    }

    fun getBuyerSearchSuggestions(str : String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        CraftExchangeRepository
            .getSearchService()
            .getSuggestions(token,str).enqueue(object : Callback, retrofit2.Callback<SuggestionResponse> {
                override fun onFailure(call: Call<SuggestionResponse>, t: Throwable) {
                    t.printStackTrace()
                    buySugListener?.onFailureSugg()
                }
                override fun onResponse(
                    call: Call<SuggestionResponse>,
                    response: Response<SuggestionResponse>
                ) {
                    if(response.body()?.valid == true){
                        buySugListener?.onSuccessSugg(response.body()!!)
                    }else{
                        buySugListener?.onFailureSugg()
                    }
                }
            })
    }
}