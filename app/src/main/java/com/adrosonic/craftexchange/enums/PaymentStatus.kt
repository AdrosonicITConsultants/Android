package com.adrosonic.craftexchange.enums

enum class PaymentStatus {
    ADVANCE,FINAL
}

fun PaymentStatus.getId() : Long {
    return when (this) {
        PaymentStatus.ADVANCE -> 1L
        PaymentStatus.FINAL -> 2L
    }
}