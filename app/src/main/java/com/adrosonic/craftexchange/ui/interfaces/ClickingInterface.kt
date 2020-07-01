package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.database.entities.ProductListRecyclerView

interface ClickingInterface {
    fun onItemClick(product: ProductListRecyclerView)
}