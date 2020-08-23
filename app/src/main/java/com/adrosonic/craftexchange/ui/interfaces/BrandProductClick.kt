package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.database.entities.realmEntities.BrandList

interface BrandProductClick {
    fun onItemClick(list : BrandList)
}