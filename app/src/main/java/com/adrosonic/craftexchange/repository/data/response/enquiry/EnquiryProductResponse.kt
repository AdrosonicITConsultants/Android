package com.adrosonic.craftexchange.repository.data.response.enquiry

import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.Product

class EnquiryProductResponse (
    val data: Product,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)




