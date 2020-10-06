package com.adrosonic.craftexchangemarketing.ui.interfaces

import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.productCatalogue.ProductImage
import com.adrosonic.craftexchangemarketing.repository.data.response.clusterResponse.Cluster

interface MoreProductClick {
    fun onItemClick(prod : ProductImage)
}