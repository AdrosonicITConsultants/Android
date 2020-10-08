package com.adrosonic.craftexchangemarketing.ui.interfaces

import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts

interface ArtisanProductClick {
    fun onItemClick(list : ArtisanProducts)
}