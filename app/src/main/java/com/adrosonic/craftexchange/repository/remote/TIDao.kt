package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiPreviewRequest
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.taxInv.TaxInvoiceResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TIDao {

    @Headers("Accept: application/json")
    @POST("enquiry/generateTaxInvoice")
    fun generateTaxInvoice(@Header("Authorization") token:String,
                           @Body invoicerequest: SendTiRequest) : Call<TaxInvoiceResponse>

    @Headers("Accept: application/json")
    @POST("enquiry/generateTaxInvoicePreview")
    fun generateTaxInvoicePreview(@Header("Authorization") token:String,
                           @Body invoicerequest: SendTiPreviewRequest) : Call<ResponseBody>

    @Headers("Accept: application/pdf")
    @GET("/enquiry/getTaxInvPDFoice")
    fun getPreviewTaxInvPDF(@Header("Authorization") token:String,
                            @Query("enquiryId") enquiryId : Int,
                            @Query("isOld") isOld : String) : Call<ResponseBody>


    @Headers("Accept: text/html")
    @GET("/enquiry/getTaxInvoicePreviewHTML")
    fun getPreviewTaxInvHTML(@Header("Authorization") token:String,
                             @Query("enquiryId") enquiryId : Int,
                             @Query("isOld") isOld : String) : Call<ResponseBody>
}