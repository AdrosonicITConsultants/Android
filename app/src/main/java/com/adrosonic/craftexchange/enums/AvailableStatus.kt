package com.adrosonic.craftexchange.enums

enum class AvailableStatus {
    IN_STOCK,MADE_TO_ORDER
}

fun AvailableStatus.getId() : Long {
    return when (this) {
        AvailableStatus.MADE_TO_ORDER -> 1L
        AvailableStatus.IN_STOCK -> 2L
    }
}