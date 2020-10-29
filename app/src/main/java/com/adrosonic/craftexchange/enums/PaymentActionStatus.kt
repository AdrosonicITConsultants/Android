package com.adrosonic.craftexchange.enums

enum class PaymentActionStatus {
    ACCEPT,REJECT
}

fun PaymentActionStatus.getId() : Long {
    return when (this) {
        PaymentActionStatus.ACCEPT -> 1L
        PaymentActionStatus.REJECT -> 2L
    }
}