package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.RelatedProducts
import java.lang.Exception

class RelateProductPredicates {
    companion object {
        private var nextID: Long? = 0

        fun getRelatedProductOfProduct(productId: Long?): RelatedProducts? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(RelatedProducts::class.java)
                .equalTo("relatedToProductId", productId)
                .limit(1)
                .findFirst()
        }

        fun insertRelatedProduct(productId: Long?,productTypeId:Long?,width:String,length:String): Long? {
            val realm = CXRealmManager.getRealmInstance()
            try {
                Log.e("insertRelatedProduct", "111111111111 : ${productId}")
                realm.executeTransaction {
                    Log.e("insertRelatedProduct", "productId : ${productId}")
                    var primId = it.where<RelatedProducts>(RelatedProducts::class.java).max("_id")
                    if (primId == null) {
                        nextID = 1
                    } else {
                        nextID = primId.toLong() + 1
                    }
                    Log.e("insertRelatedProduct", "productTypeId : ${productTypeId}")
                    var relatedObj = it.createObject(RelatedProducts::class.java, nextID)
                    relatedObj.relatedToProductId = productId
                    relatedObj. productTypeId= productTypeId
                    relatedObj.productWidth = width
                    relatedObj.productLength = length
                    Log.e("insertRelatedProduct", "width : ${width}")
                    relatedObj.inProductCategoryId = 0
                    relatedObj.relatedProductId = 0
                    relatedObj.productName = ""
                    relatedObj.productWeight = ""
                    Log.e("insertRelatedProduct", "length : ${length}")
                }
            } catch (e: Exception) {
                //print logs
                Log.e("insertRelatedProduct", "exception : ${e.message}")
            } finally {
//                realm.close()
            }
            return nextID
        }
        fun deleteRelatedProduct(id:Long){
            var count=0
            val realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction {
                val artisonProd = it.where(RelatedProducts::class.java).equalTo("relatedToProductId", id).findAll()
                artisonProd.deleteAllFromRealm()
            }

        }
    }
}