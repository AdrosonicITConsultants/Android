package com.adrosonic.craftexchange.enums

enum class ActionForm {
    SAVE,SEND
}

fun ActionForm.getId() : Long {
    return when (this) {
        ActionForm.SAVE -> 0L
        ActionForm.SEND -> 1L
    }
}