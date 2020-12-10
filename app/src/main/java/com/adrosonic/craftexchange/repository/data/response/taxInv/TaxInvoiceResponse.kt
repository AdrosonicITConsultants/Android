package com.adrosonic.craftexchange.repository.data.response.taxInv

data class TaxInvoiceResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val id: Long,
    val enquiryId: Long,
    val hsn: Long,
    val ppu: Long,
    val quantity: Long,
    val piAmt: Long,
    val date: String,
    val artisanId: Long,
    val invoiceNo: String,
    val advancePaidAmt: Long,
    val deliveryCharges: Long,
    val cgst: Double,
    val sgst: Double,
    val finalTotalAmt: Long,
    val isactive: Long,
    val createdon: String,
    val modifiedon: String
)