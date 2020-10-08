package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.SearchPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.WishlistPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.search.SearchProduct
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SearchProductResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SuggestionResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
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

    interface FetchBuyerSearchProducts{
        fun onSuccessSearch(search : SearchProductResponse)
        fun onFailureSearch()
    }

    interface FetchBuyerWishlistedProducts{
        fun onWSuccess()
        fun onWFailure()
    }

    var artSugListener: FetchArtisanSuggestions? = null
    var buySugListener: FetchBuyerSuggestions? = null

    var buySearchListener : FetchBuyerSearchProducts ? = null
    var wishListener : FetchBuyerWishlistedProducts ?= null

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

        craftexchangemarketingRepository
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

        craftexchangemarketingRepository
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

    fun getProductsForBuyer(str : String , id : Long, pageNo : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        craftexchangemarketingRepository
            .getSearchService()
            .searchProducts(token, SearchProduct(pageNo,str,id))
            .enqueue(object : Callback, retrofit2.Callback<SearchProductResponse> {
                override fun onFailure(call: Call<SearchProductResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("BuyerSearchResults","Failure :"+t.printStackTrace().toString())
                    buySearchListener?.onFailureSearch()
                }
                override fun onResponse(
                    call: Call<SearchProductResponse>,
                    response: Response<SearchProductResponse>
                ) {
                    if(response.body()?.data != null) {
                        Log.e("BuyerSearchResults", "Success : " + response?.body()?.data)
                        buySearchListener?.onSuccessSearch(response?.body()!!)
                    }else{
                        Log.e("BuyerSearchResults","Failure :")
                        buySearchListener?.onFailureSearch()
                    }
                }
            })
    }

    fun getwishlisteProductIds(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getWishlistService()
            .getWishlistedProductIds(token)
            .enqueue(object: Callback, retrofit2.Callback<WishListedIds> {
                override fun onFailure(call: Call<WishListedIds>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","wishlist onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<WishListedIds>,
                    response: retrofit2.Response<WishListedIds>) {

                    if(response.body()?.valid == true){
                        val response=response.body()?.data
                        Log.e("LandingViewModel","wishlist :"+response?.joinToString())

                        WishlistPredicates.addToWishlist(response)
                        wishListener?.onWSuccess()

                    }else{
                        wishListener?.onWFailure()
                        Log.e("LandingViewModel","wishlist onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }
}