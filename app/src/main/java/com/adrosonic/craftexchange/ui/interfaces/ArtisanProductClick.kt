package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts

interface ArtisanProductClick {
    fun onItemClick(list : ArtisanProducts)
}