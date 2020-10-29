package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.database.entities.realmEntities.CategoryProducts

interface CategoryProductClick {
    fun onItemClick(list : CategoryProducts)
}