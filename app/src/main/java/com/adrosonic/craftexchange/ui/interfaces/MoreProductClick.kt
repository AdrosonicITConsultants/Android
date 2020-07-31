package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.ProductImage
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.Cluster

interface MoreProductClick {
    fun onItemClick(prod : ProductImage)
}