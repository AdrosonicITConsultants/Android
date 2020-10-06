package com.adrosonic.craftexchangemarketing.ui.interfaces

import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CategoryProducts

interface CategoryProductClick {
    fun onItemClick(list : CategoryProducts)
}