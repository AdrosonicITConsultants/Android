package com.adrosonic.craftexchangemarketing.repository.remote

//import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
//import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiPreviewRequest
//import com.adrosonic.craftexchangemarketing.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.taxInv.TaxInvPreviewResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.taxInv.TaxInvoiceResponse
import com.adrosonic.craftexchangemarketing.repository.data.request.taxInv.SendTiPreviewRequest
import com.adrosonic.craftexchangemarketing.repository.data.request.taxInv.SendTiRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TIDao {

    @Headers("Accept: application/json")
    @POST("api/enquiry/generateTaxInvoice")
    fun generateTaxInvoice(@Header("Authorization") token:String,
                           @Body invoicerequest: SendTiRequest
    ) : Call<TaxInvoiceResponse>

    @Headers("Accept: application/json")
    @POST("api/enquiry/generateTaxInvoicePreview")
    fun generateTaxInvoicePreview(@Header("Authorization") token:String,
                           @Body invoicerequest: SendTiPreviewRequest
    ) : Call<TaxInvPreviewResponse>

    @Headers("Accept: application/pdf")
    @GET("api/enquiry/getTaxInvoicePDF")
    fun getPreviewTaxInvPDF(@Header("Authorization") token:String,
                            @Query("enquiryId") enquiryId : Int,
                            @Query("isOld") isOld : String) : Call<ResponseBody>

    @Headers("Accept: text/html")
    @GET("api/enquiry/getTaxInvoicePreviewHTML")
    fun getPreviewTaxInvHTML(@Header("Authorization") token:String,
                             @Query("enquiryId") enquiryId : Int,
                             @Query("isOld") isOld : String) : Call<ResponseBody>
}