package com.adrosonic.craftexchangemarketing.enums

enum class RedirectEnqTypes {
    CUSTOM,FAULTY,OTHERS
}

fun RedirectEnqTypes.getId() : Long {
    return when (this) {
        RedirectEnqTypes.CUSTOM -> 0L
        RedirectEnqTypes.FAULTY -> 1L
        RedirectEnqTypes.OTHERS -> 2L
    }
}

fun RedirectEnqTypes.getString() : String {
    return when (this) {
        RedirectEnqTypes.CUSTOM ->"Custom"
        RedirectEnqTypes.FAULTY -> "Faulty"
        RedirectEnqTypes.OTHERS -> "Others"
    }
}