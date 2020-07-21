package com.adrosonic.craftexchange.ui.interfaces

import com.adrosonic.craftexchange.repository.data.response.clusterResponse.Cluster

interface ClusterProductClick {
    fun onItemClick(list : Cluster)
}