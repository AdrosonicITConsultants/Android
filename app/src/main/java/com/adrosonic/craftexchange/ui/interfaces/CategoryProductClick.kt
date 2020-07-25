package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.Product

interface CategoryProductClick {
    fun onItemClick(list : Product)
}