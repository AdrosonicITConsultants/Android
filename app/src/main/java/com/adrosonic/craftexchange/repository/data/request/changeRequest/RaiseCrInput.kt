package com.adrosonic.craftexchange.repository.data.request.changeRequest

data class RaiseCrInput (
    val enquiryId: Long,
    val itemList: List<ItemList>
)

data class ItemList (
    val changeRequestId: Long,
    val id: Long,
    val requestItemsId: Long,
    val requestStatus: Long,
    val requestText: String
)
