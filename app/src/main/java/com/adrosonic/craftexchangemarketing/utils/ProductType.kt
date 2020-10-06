package com.adrosonic.craftexchangemarketing.utils

enum class ProductType {
    CATEGORY,CLUSTER,BRANDS
}
fun ProductType.displayName() : String {
    return when (this) {
        ProductType.CATEGORY -> "Cat"
        ProductType.CLUSTER -> "Drafts"
        ProductType.BRANDS -> "Sent Items"
        else -> "Other"
    }
}
