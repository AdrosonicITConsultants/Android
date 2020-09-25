package com.adrosonic.craftexchange.enums

enum class EnquiryStatus {
    ONGOING,COMPLETED
}

fun EnquiryStatus.getId() : Long {
    return when (this) {
        EnquiryStatus.COMPLETED -> 1L
        EnquiryStatus.ONGOING -> 2L
    }
}