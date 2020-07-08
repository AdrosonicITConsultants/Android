package com.adrosonic.craftexchange.database.predicates

import com.adrosonic.craftexchange.database.entities.UserProductCategory
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import io.realm.Realm
import java.lang.Exception

class ProductPredicates {
    companion object {
        private var nextID: Long? = 0

        fun insertProductCategory(user: ProfileResponse?) : Long? {
            nextID = 0L
            val realm = Realm.getDefaultInstance()
            var productList = user?.data?.userProductCategories
            try {
                realm?.executeTransaction {
                    var prodIterator = productList?.iterator()
                    if (prodIterator != null) {
                        while (prodIterator.hasNext()) {
                            var prod = prodIterator.next()
                            var prodObj = realm.where(UserProductCategory::class.java)
                                .equalTo("id", prod.id)
                                .limit(1)
                                .findFirst()

                            if (prodObj == null) {
                                var primId = it.where(UserProductCategory::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exprod = it.createObject(UserProductCategory::class.java, nextID)
                                exprod.id = prod.id
                                exprod.userid = prod.userId
                                exprod.productCategoryid = prod.productCategoryId

                                realm.copyToRealmOrUpdate(exprod)
                            } else {
                                nextID = prodObj._id ?: 0
                                prodObj.id = prod.id
                                prodObj.userid = prod.userId
                                prodObj.productCategoryid = prod.productCategoryId

                                realm.copyToRealmOrUpdate(prodObj)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            } finally {
//                realm.close()
            }
            return nextID
        }

    }
}