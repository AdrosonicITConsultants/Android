package com.adrosonic.craftexchange.enums

enum class InnerEnquiryStages {
    YARN_PROCURED,
    YARN_DYE,
    PRE_LOOM_PROCESS,
    WEAVING,
    POST_LOOM_PROCESS
}

fun InnerEnquiryStages.getId() : Long {
    return when (this) {
        InnerEnquiryStages.YARN_PROCURED -> 1L
        InnerEnquiryStages.YARN_DYE -> 2L
        InnerEnquiryStages.PRE_LOOM_PROCESS -> 3L
        InnerEnquiryStages.WEAVING -> 4L
        InnerEnquiryStages.POST_LOOM_PROCESS -> 5L
    }
}