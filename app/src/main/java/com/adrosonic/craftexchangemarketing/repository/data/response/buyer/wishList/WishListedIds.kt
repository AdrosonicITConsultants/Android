package com.adrosonic.craftexchangemarketing.repository.data.response.buyer.wishList

data class WishListedIds (
    val data: List<Long>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)
