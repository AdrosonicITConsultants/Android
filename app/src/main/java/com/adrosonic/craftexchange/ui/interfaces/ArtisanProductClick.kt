package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard

interface ArtisanProductClick {
    fun onItemClick(list : ProductCard)
}