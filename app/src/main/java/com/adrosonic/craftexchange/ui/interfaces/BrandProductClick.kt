package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandDetails

interface BrandProductClick {
    fun onItemClick(list : BrandDetails)
}