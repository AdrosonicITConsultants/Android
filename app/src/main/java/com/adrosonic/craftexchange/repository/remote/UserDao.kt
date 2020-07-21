package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditDetailsResponse
import com.adrosonic.craftexchange.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.PaymentAccountDetails
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditBankDetailsResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserDao {

    @Headers("Accept: application/json")
    @GET("user/myprofile")
    fun viewMyProfile(@Header("Authorization") token:String) : Call<ProfileResponse>

    @Headers("Accept: application/json")
    @PUT("user/edit/buyerProfile")
    fun editBuyerDetailsPhoto(@Header("Content-Type") headerValue:String,
                              @Header("Authorization") token:String,
                              @Query("profileDetails") profileDetails : String,
                              @Body logo : MultipartBody
    ): Call<EditProfileResponse>

    @Headers("Accept: application/json")
    @PUT("user/edit/buyerProfile")
    fun editBuyerDetails(@Header("Authorization") token:String,
                         @Query("profileDetails", encoded = false) profileDetails : String): Call<EditProfileResponse>

    @Headers("Accept: application/json")
    @PUT("user/edit/artistProfile")
    fun editArtisanProfileDetailsPhoto(@Header("Content-Type") headerValue:String,
                                       @Header("Authorization") token:String,
                                       @Query("address") address : String,
                                       @Body profilePic : MultipartBody?
    ): Call<EditDetailsResponse>

    @Headers("Accept: application/json")
    @PUT("user/edit/artistProfile")
    fun editArtisanProfileDetails(@Header("Authorization") token:String,
                                  @Query("address", encoded = false) address : String): Call<EditDetailsResponse>

    @Headers("Accept: application/json")
    @PUT("user/edit/artistBrandDetails")
    fun editArtisanBrandDetailsPhoto(@Header("Content-Type") headerValue:String,
                                @Header("Authorization") token:String,
                                @Query("editBrandDetails") editBrandDetails : String,
                                @Body logo : MultipartBody
    ): Call<EditDetailsResponse>

    @Headers("Accept: application/json")
    @PUT("user/edit/artistBrandDetails")
    fun editArtisanBrandDetails(@Header("Authorization") token:String,
                           @Query("editBrandDetails", encoded = false) editBrandDetails : String): Call<EditDetailsResponse>


    @Headers("Accept: application/json")
    @PUT("user/edit/bankDetails")
    fun editArtisanBankDetails(@Header("Authorization") token:String,
                            @Body paymentAccountDetails : ArrayList<PaymentAccountDetails>) : Call<EditBankDetailsResponse>
}