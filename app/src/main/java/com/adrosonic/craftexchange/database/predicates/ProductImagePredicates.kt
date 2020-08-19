package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductImages
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.ProductImage
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
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                val artisonProd = it.where(ProductImages::class.java).equalTo("productId", id).findAll()
                artisonProd.deleteAllFromRealm()
            }

        }

        fun deleteProdImageForUpdate(id:Long){
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                Log.e("Offline", "deleteProdImageForUpdate id:" + id)
                val artisonProd = it.where(ProductImages::class.java).equalTo(ProductImages.COLUMN_PRODUCT_ID, id).findAll()
                Log.e("Offline", "deleteProdImageForUpdate size : ${artisonProd.size}")
                artisonProd.deleteAllFromRealm()
            }

        }
        fun insertBuyerCustomProdImages(imageList:ArrayList<ProductImage>){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm?.executeTransaction {
                    val imageIterator = imageList.iterator()
                    Log.e("insertProductImages", "imagelist : ${imageList.size}")
                    while (imageIterator.hasNext()) {
                        var image=imageIterator.next()
                        var dbProdObj = realm.where(ProductImages::class.java).
                        equalTo(ProductImages.COLUMN_PRODUCT_ID,image.productId).and().
                        equalTo(ProductImages.COLUMN_IMAGE_ID,image.id).and().
                        equalTo(ProductImages.COLUMN_IMAGE_NAME,image.lable).findAll()

                        if(dbProdObj.size>0){}
                        else {
                            var primId =
                                it.where<ProductImages>(ProductImages::class.java).max("_id")
                            if (primId == null) {
                                nextID = 1
                            } else {
                                nextID = primId.toLong() + 1
                            }
                            Log.e("insertProductImages", "nextID : ${nextID}")
                            var relatedObj = it.createObject(ProductImages::class.java, nextID)
                            relatedObj.productId = image.productId
                            relatedObj.imageName = image.lable
                            relatedObj.imageId = image.id
                            Log.e("insertProductImages", "enddddddd ")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("insertProductImages", "exception : ${e.printStackTrace()}")
            } finally {
//                realm.close()
            }
        }
    }
}