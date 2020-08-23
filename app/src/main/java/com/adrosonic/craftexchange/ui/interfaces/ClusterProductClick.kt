package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList

interface ClusterProductClick {
    fun onItemClick(list : ClusterList)
}