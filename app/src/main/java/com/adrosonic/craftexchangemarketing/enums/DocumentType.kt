package com.adrosonic.craftexchangemarketing.enums


enum class DocumentType {
    ADVANCEPAY,FINALPAY,DELIVERY_CHALLAN
}

fun DocumentType.getId() : Long {
    return when (this) {
        DocumentType.ADVANCEPAY -> 1L
        DocumentType.FINALPAY -> 2L
        DocumentType.DELIVERY_CHALLAN -> 3L
    }
}