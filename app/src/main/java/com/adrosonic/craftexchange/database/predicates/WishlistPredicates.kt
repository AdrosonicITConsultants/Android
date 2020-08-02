package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import java.lang.Exception

class WishlistPredicates {

    companion object {
        private var nextID: Long? = 0

        fun addToWishlist(productId: Long?){
            var realm = CXRealmManager.getRealmInstance()
            var product = realm.where(ProductCatalogue::class.java).equalTo("productId",ProductCatalogue.COLUMN_PRODUCT_ID).limit(1).findFirst()
            product?.actionWishlisted = 1
        }

        fun getWishlistedProduct(productId: Long?){

        }

        fun updateProductWishlisting(productId: Long?,isWishlisted : Long?){
            var realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction{
                var product = realm.where(ProductCatalogue::class.java).equalTo(ProductCatalogue.COLUMN_PRODUCT_ID,productId).limit(1).findFirst()
                product?.isWishlisted = isWishlisted
            }
        }

        fun getProductMarkedForActions(actionsMarked:String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId=ArrayList<Long>()
            try {
                realm?.executeTransaction {
                    var message= when(actionsMarked){
                        "actionWishlisted=1"-> {realm.where(ProductCatalogue::class.java)
                            .equalTo(ProductCatalogue.COLUMN_ACTION_WISHLISTED,1L)
                            .findAll()
                        }
                        else-> null
                    }
                    if(message!=null){
                        val iterator=message.iterator()
                        while (iterator.hasNext()) {
                            itemId?.add(iterator.next()._id?:0L)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("offline","while fetching actions : "+e.message)
            } finally {
                realm.close()
            }
            return itemId
        }


    }
}