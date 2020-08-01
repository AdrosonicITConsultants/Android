package com.adrosonic.craftexchange.database.predicates

import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue

class WishlistPredicates {

    companion object {
        private var nextID: Long? = 0

        fun addToWishlist(productId: Long?){
            var realm = CXRealmManager.getRealmInstance()
            var product = realm.where(ProductCatalogue::class.java).equalTo("productId",ProductCatalogue.COLUMN_PRODUCT_ID).limit(1).findFirst()


        }

        fun getWishlistedProduct(productId: Long?){

        }

    }
}