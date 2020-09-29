package com.adrosonic.craftexchange.enums


enum class EnquiryStages {
    ENQUIRY_GENERATED,
    MOQ_DECIDED,
    PI_FINALIZED,
    ADVANCE_PAYMENT_RECEIVED,
    PRODUCTION_COMPLETED,
    COMPLETION_OF_ORDER,
    QUALITY_CHECK_BEFORE_DELIVERY,
    FINAL_INVOICE_RAISED,
    FINAL_PAYMENT_RECEIVED,
    ORDER_DISPATCHED
}

fun EnquiryStages.getId() : Long {
    return when (this) {
        EnquiryStages.ENQUIRY_GENERATED -> 1L
        EnquiryStages.MOQ_DECIDED -> 2L
        EnquiryStages.PI_FINALIZED -> 3L
        EnquiryStages.ADVANCE_PAYMENT_RECEIVED -> 4L
        EnquiryStages.PRODUCTION_COMPLETED -> 5L
        EnquiryStages.COMPLETION_OF_ORDER -> 6L
        EnquiryStages.QUALITY_CHECK_BEFORE_DELIVERY -> 7L
        EnquiryStages.FINAL_INVOICE_RAISED -> 8L
        EnquiryStages.FINAL_PAYMENT_RECEIVED -> 9L
        EnquiryStages.ORDER_DISPATCHED -> 10L
    }
}