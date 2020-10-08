package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductImages
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.WeaveTypes
import com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate.ProductWeaf
import java.lang.Exception

class WeaveTypesPredicates {
    companion object {
        private var nextID: Long? = 0

        fun insertWeaveIds(productId: Long?,weaveIds:List<Long>?): Long? {
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    val imageIterator = weaveIds?.iterator()
                    Log.e("insertWeaveIds", "000000000 "+ weaveIds?.joinToString())
                    while (imageIterator!!.hasNext()) {
                        var id=imageIterator.next()
                        var primId = it.where<WeaveTypes>(WeaveTypes::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        Log.e("insertWeaveIds", "11111111111 "+ nextID)
                        var weaveObj = it.createObject(
                            WeaveTypes::class.java,
                            nextID
                        )
                        weaveObj.productId = productId
                        weaveObj.weaveId = id
                        weaveObj.productWeaveId = 0
                        Log.e("insertWeaveIds", "222222222 "+  weaveObj.weaveId)
                    }
                }
            } catch (e: Exception) {
                //print logs
                Log.e("insertWeaveIds", "exception : ${e.printStackTrace()}")
            } finally {
//                realm.close()
            }
            return nextID
        }

        fun insertWeaveIds(weaveIds:List<ProductWeaf>?) {
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    val imageIterator = weaveIds?.iterator()
                    Log.e("insertWeaveIds", "000000000 "+ weaveIds?.joinToString())
                    while (imageIterator!!.hasNext()) {
                        var id=imageIterator.next()
                        var primId = it.where<WeaveTypes>(WeaveTypes::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        Log.e("insertWeaveIds", "11111111111 "+ nextID)
                        var weaveObj = it.createObject(
                            WeaveTypes::class.java,
                            nextID
                        )
                        weaveObj.productId = id.productId
                        weaveObj.weaveId = id.weaveId
                        weaveObj.productWeaveId = 0
                        Log.e("insertWeaveIds", "222222222 "+  weaveObj.weaveId)
                    }
                }
            } catch (e: Exception) {
                //print logs
                Log.e("insertWeaveIds", "exception : ${e.printStackTrace()}")
            } finally {
//                realm.close()
            }
        }


        fun getWeaveList(id:Long):ArrayList<Long>{
            var list=ArrayList<Long>()
            var realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var images = realm.where(WeaveTypes::class.java).equalTo("productId", id).findAll()
                    images.forEach { list.add(it.weaveId?:0) }
                }
            }catch(e : Exception){
            }finally{
                realm.close()
            }
            return list
        }

        fun deleteWeaveIds(id:Long?){
            var count=0
            val realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction {
                val artisonProd = it.where(WeaveTypes::class.java).equalTo("productId", id).findAll()
                artisonProd.deleteAllFromRealm()
            }

        }
    }
}