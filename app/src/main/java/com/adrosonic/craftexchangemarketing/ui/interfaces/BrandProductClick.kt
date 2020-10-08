package com.adrosonic.craftexchangemarketing.ui.interfaces

import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.BrandList

interface BrandProductClick {
    fun onItemClick(list : BrandList)
}