package com.adrosonic.craftexchange.enums

enum class OrderStatus {
    ONGOING,COMPLETED
}

fun OrderStatus.getId() : Long {
    return when (this) {
        OrderStatus.COMPLETED -> 1L
        OrderStatus.ONGOING -> 0L
    }
}