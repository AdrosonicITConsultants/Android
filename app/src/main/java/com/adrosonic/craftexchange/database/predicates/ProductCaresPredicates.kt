package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCares
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductImages
import com.adrosonic.craftexchange.database.entities.realmEntities.WeaveTypes
import java.lang.Exception

class ProductCaresPredicates {
    companion object {
        private var nextID: Long? = 0

        fun insertCareIds(productId: Long?,careIds:List<Long>?): Long? {
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm?.executeTransaction {
                    val imageIterator = careIds?.iterator()
                    while (imageIterator!!.hasNext()) {
                        var id=imageIterator.next()
                        var primId = it.where<ProductCares>(ProductCares::class.java).max("_id")
                        if (primId == null) nextID = 1
                        else  nextID = primId.toLong() + 1

                        var weaveObj = it.createObject(ProductCares::class.java, nextID)
                        weaveObj.productId = productId
                        weaveObj.careId = id
                        weaveObj.productCareId = 0
                    }
                }
            } catch (e: Exception) {
                Log.e("insertCareIds", "exception : ${e.printStackTrace()}")
            } finally {
//                realm.close()
            }
            return nextID
        }
        fun getCareList(id:Long):List<Long>{
            var list=ArrayList<Long>()
            var realm = CXRealmManager.getRealmInstance()
            try {
                realm?.executeTransaction {
                    var images = realm.where(ProductCares::class.java).equalTo("productId", id).findAll()
                    images.forEach { list.add(it.careId?:0) }
                }
            }catch(e : Exception){
            }finally{
                realm.close()
            }
            return list
        }
        fun deleteCareIds(id:Long){
            var count=0
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                val artisonProd = it.where(ProductCares::class.java).equalTo("productId", id).findAll()
                artisonProd.deleteAllFromRealm()
            }

        }
    }
}