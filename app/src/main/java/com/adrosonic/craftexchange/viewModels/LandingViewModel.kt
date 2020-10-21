package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.NotificationPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchange.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.InnerStageData
import com.adrosonic.craftexchange.repository.data.response.logout.LogoutResponse
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.repository.data.response.qc.QCQuestionData
import com.adrosonic.craftexchange.repository.data.response.transaction.TransactionStatusData
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LandingViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "LandingViewModel"
    }

    interface wishlistFetchedInterface{
        fun onSuccess()
        fun onFailure()
    }
    interface notificationInterface{
        fun onNotificationDataFetched()
    }
    var listener: wishlistFetchedInterface? = null
    var noficationlistener: notificationInterface? = null
    val wishListData : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }

    fun getwishListMutableData(): MutableLiveData<RealmResults<ProductCatalogue>> {
        wishListData.value=loadwishListData()
        return wishListData
    }
    fun loadwishListData(): RealmResults<ProductCatalogue> {
        var wishList= WishlistPredicates.getWishListedData()
        Log.e("Wishlist","loadwishListData :"+wishList?.size)
        return wishList!!
    }

    fun logoutUser() {
        val editor = Prefs.edit()
        editor.clear()
        editor.commit()
        editor.apply()
        calllogoutUser()
    }

    fun getProductsOfArtisan(context : Context){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getArtisanProducts(token)
            .enqueue(object: Callback, retrofit2.Callback<ArtisanProductDetailsResponse> {
                override fun onFailure(call: Call<ArtisanProductDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<ArtisanProductDetailsResponse>,
                    response: retrofit2.Response<ArtisanProductDetailsResponse>) {
                    if(response.body()?.valid == true){
                        ProductPredicates.insertProductsOfArtisan(response.body())
                    }else{
                        Utility.displayMessage(response.body()?.errorMessage.toString(),context)
                    }
                }

            })
    }

    fun getProductUploadData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getProductUploadData(token)
            .enqueue(object: Callback, retrofit2.Callback<ProductUploadData> {
                override fun onFailure(call: Call<ProductUploadData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getProductUploadData onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ProductUploadData>,
                    response: retrofit2.Response<ProductUploadData>) {

                    if(response.body()?.valid == true){
                        //todo store data to spf
                        Log.e("LandingViewModel","getProductUploadData :"+response.body()?.data?.productCare?.size)
                        UserConfig.shared.productUploadJson= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF :"+UserConfig.shared.productUploadJson)

                    }else{
                        Log.e("LandingViewModel","getProductUploadData onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getEnquiryStageData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getAllEnquiryStagesData(token)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryStageData> {
                override fun onFailure(call: Call<EnquiryStageData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryStageData>,
                    response: retrofit2.Response<EnquiryStageData>) {

                    if(response.body()?.valid == true){

                        UserConfig.shared.enquiryStageData= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF Enquiries :"+UserConfig.shared.enquiryStageData)

                    }else{
                        Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getInnerEnquiryStageData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getInnerEnquiryStagesData(token)
            .enqueue(object: Callback, retrofit2.Callback<InnerStageData> {
                override fun onFailure(call: Call<InnerStageData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<InnerStageData>,
                    response: retrofit2.Response<InnerStageData>) {

                    if(response.body()?.valid == true){

                        UserConfig.shared.innerEnquiryStageData= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF Enquiries :"+UserConfig.shared.innerEnquiryStageData)

                    }else{
                        Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getQCStageData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getQCService()
            .getQCStages(token)
            .enqueue(object: Callback, retrofit2.Callback<InnerStageData> {
                override fun onFailure(call: Call<InnerStageData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getAllQCStages onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<InnerStageData>,
                    response: retrofit2.Response<InnerStageData>) {
                    if(response.body()?.valid == true){
                        UserConfig.shared.qcStageData= Gson().toJson(response.body())
                    }else{
                        Log.e("LandingViewModel","getAllQCStages onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getQCQuestionData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getQCService()
            .getQCQuestionData(token)
            .enqueue(object: Callback, retrofit2.Callback<QCQuestionData> {
                override fun onFailure(call: Call<QCQuestionData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getAllQCStages onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<QCQuestionData>,
                    response: retrofit2.Response<QCQuestionData>) {
                    if(response.body()?.valid == true){
                        UserConfig.shared.qcQuestionData= Gson().toJson(response.body())
                    }else{
                        Log.e("LandingViewModel","getAllQCStages onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

//    fun getProgressTimeData(){
//        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
//        CraftExchangeRepository
//            .getEnquiryService()
//            .getProgressTimelineData(token)
//            .enqueue(object: Callback, retrofit2.Callback<InnerStageData> {
//                override fun onFailure(call: Call<InnerStageData>, t: Throwable) {
//                    t.printStackTrace()
//                    Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+t.message)
//                }
//                override fun onResponse(
//                    call: Call<InnerStageData>,
//                    response: retrofit2.Response<InnerStageData>) {
//
//                    if(response.body()?.valid == true){
//
//                        UserConfig.shared.progressTimeData= Gson().toJson(response.body())
//                        Log.e("LandingViewModel","SPF Enquiries :"+UserConfig.shared.progressTimeData)
//
//                    }else{
//                        Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+response.body()?.errorCode)
//
//                    }
//                }
//
//            })
//    }

    fun getEnquiryStageAvailableProdsData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getAvailableProdEnquiryStagesData(token)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryAvaProdStageData> {
                override fun onFailure(call: Call<EnquiryAvaProdStageData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryAvaProdStageData>,
                    response: retrofit2.Response<EnquiryAvaProdStageData>) {

                    if(response.body()?.valid == true){

                        UserConfig.shared.enquiryAvaProdStageData= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF Enquiries :"+UserConfig.shared.enquiryAvaProdStageData)

                    }else{
                        Log.e("LandingViewModel","getAvailableProdEnquiriesStages onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getwishlisteProductIds(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getWishlistService()
            .getWishlistedProductIds(token)
            .enqueue(object: Callback, retrofit2.Callback<WishListedIds> {
                override fun onFailure(call: Call<WishListedIds>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                    Log.e("LandingViewModel","wishlist onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<WishListedIds>,
                    response: Response<WishListedIds>) {

                    if(response.body()?.valid == true){
                        val response=response.body()?.data
                        Log.e("LandingViewModel","wishlist :"+response?.joinToString())
                        response?.forEach {
                            if(!WishlistPredicates.isProductPresent(it))   getBuyerProductDetails(it)
                        }
                        WishlistPredicates.addToWishlist(response)
                        listener?.onSuccess()

                    }else{
                        listener?.onFailure()
                        Log.e("LandingViewModel","wishlist onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getBuyerProductDetails(id:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getWishlistService()
            .getSingleProductDetails(token,id.toInt())
            .enqueue(object: Callback, retrofit2.Callback<SingleProductDetails> {
                override fun onFailure(call: Call<SingleProductDetails>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","ownproduct onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<SingleProductDetails>,
                    response: retrofit2.Response<SingleProductDetails>) {
                    if(response.body()?.valid == true){
                        val response=response.body()?.data
                        if (response != null) {
                            WishlistPredicates.insertSingleProduct(response)
                        }
                        Log.e("LandingViewModel","own product :"+response?.artistName)
                    }
                }

            })
    }

    fun getArtisanBrandDetails(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getFilteredArtisans(token).enqueue(object : Callback, retrofit2.Callback<BrandListResponse> {
                override fun onFailure(call: Call<BrandListResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<BrandListResponse>,
                    response: Response<BrandListResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ProductPredicates.insertBrands(response.body())
                    }
                }
            })
    }

    fun getAllNotifications(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getNotificationService()
            .getAllNotifications(token)
            .enqueue(object: Callback, retrofit2.Callback<NotificationResponse> {
                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e(TAG,"onFailure: "+t.message)
                    noficationlistener?.onNotificationDataFetched()
                }
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e(TAG,"getProductUploadData :"+response.body()?.data?.getAllNotifications?.size)
                        NotificationPredicates.insertNotification(response.body()?.data?.getAllNotifications)
                        UserConfig.shared?.notiBadgeCount=response.body()?.data?.count?:0
                        noficationlistener?.onNotificationDataFetched()
                    }else{
                        Log.e(NotificationViewModel.TAG,"getProductUploadData onFailure: "+response.body()?.errorCode)
                        noficationlistener?.onNotificationDataFetched()
                    }
                }
            })
    }

    fun calllogoutUser(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getUserService()
            .logoutUser(token,UserConfig.shared?.deviceRegistrationToken).enqueue(object : Callback, retrofit2.Callback<LogoutResponse> {
                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<LogoutResponse>,
                    response: Response<LogoutResponse>
                ) {

                }
            })
    }

    fun getMoqDeliveryTimes(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getMoqService()
            .getMoqDeliveryTimes(token).enqueue(object : Callback, retrofit2.Callback<MoqDeliveryTimesResponse> {
                override fun onFailure(call: Call<MoqDeliveryTimesResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<MoqDeliveryTimesResponse>,
                    response: Response<MoqDeliveryTimesResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"getMoqDeliveryTimes :$valid")
                    if(valid){
                        Log.e(TAG,"getMoqDeliveryTimes :${response.body()?.data?.size}")
                        Log.e(TAG,"getMoqDeliveryTimes :${Gson().toJson(response.body())}")
                        UserConfig.shared.moqDeliveryDates=Gson().toJson(response.body())
                        Log.e(TAG,"getMoqDeliveryTimes after:${UserConfig.shared.moqDeliveryDates}")
                    }
                }
            })
    }

    fun getTransactionStatus(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        CraftExchangeRepository
            .getTransactionService()
            .getTransactionStatus(token).enqueue(object : Callback, retrofit2.Callback<TransactionStatusData> {
                override fun onFailure(call: Call<TransactionStatusData>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<TransactionStatusData>,
                    data: Response<TransactionStatusData>
                ) {
                    if(data.body()?.valid == true){
                        UserConfig.shared.transactionStatusData= Gson().toJson(data.body())
                    }
                }
            })
    }
}