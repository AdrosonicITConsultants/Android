package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductImages
import com.adrosonic.craftexchange.database.entities.realmEntities.RelatedProducts
import java.lang.Exception

class ProductImagePredicates
{
    companion object {
        private var nextID: Long? = 0
        fun insertProductImages(productId: Long?,imageList:ArrayList<String>): Long? {
            val realm = CXRealmManager.getRealmInstance()
            try {

                realm?.executeTransaction {
                    val imageIterator = imageList.iterator()
                    Log.e("insertProductImages", "imagelist : ${imageList.size}")
                    while (imageIterator.hasNext()) {
                        var image=imageIterator.next()
                        var primId = it.where<ProductImages>(ProductImages::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        Log.e("insertProductImages", "nextID : ${nextID}")
                        var relatedObj = it.createObject(ProductImages::class.java, nextID)
                        relatedObj.productId = productId
                        relatedObj.imageName = image
                        relatedObj.imageId = 0
                        Log.e("insertProductImages", "enddddddd ")
                    }
                }
            } catch (e: Exception) {
                //print logs
                Log.e("insertProductImages", "exception : ${e.printStackTrace()}")
            } finally {
//                realm.close()
            }
            return 0
        }

        fun getImagesList(id:Long):ArrayList<String>{
            var list=ArrayList<String>()
            var realm = CXRealmManager.getRealmInstance()
            try {
                realm?.executeTransaction {
                    var images = realm.where(ProductImages::class.java).equalTo(ProductImages.COLUMN_PRODUCT_ID, id).findAll()
                   images.forEach { list.add(it.imageName?:"") }
                }
            }catch(e : Exception){
            }finally{
                realm.close()
            }
            return list
        }
        fun deleteProdImages(id:Long){
            var count=0
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                val artisonProd = it.where(ProductImages::class.java).equalTo("productId", id).findAll()
                artisonProd.deleteAllFromRealm()
            }

        }
    }
}