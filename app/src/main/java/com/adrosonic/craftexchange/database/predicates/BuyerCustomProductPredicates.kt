package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.RelatedProduct
import com.adrosonic.craftexchange.repository.data.request.buyer.OwnDesignRequest
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.OwnDesigns
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.ProductImage
import io.realm.RealmResults

class BuyerCustomProductPredicates {

    companion object {
        private var nextID: Long? = 0


        fun getProductId(productId: Long?): Long? {
            var realm = CXRealmManager.getRealmInstance()
            var product: Long? = 0L
            realm.executeTransaction {
                product = realm.where(BuyerCustomProduct::class.java)
                    .equalTo(BuyerCustomProduct.COLUMN_ID, productId).limit(1).findFirst()?.id
            }
            return product
        }
        fun getCustomProduct(productId: Long?): BuyerCustomProduct? {
            var realm = CXRealmManager.getRealmInstance()
            var product: BuyerCustomProduct? =null
            realm.executeTransaction {
                product = realm.where(BuyerCustomProduct::class.java).equalTo(BuyerCustomProduct.COLUMN__ID, productId).limit(1).findFirst()
            }
            return product
        }

        fun getProductMarkedForActions(actionsMarked: String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId = ArrayList<Long>()
            try {
                realm?.executeTransaction {
                    var message = when (actionsMarked) {
                        "actionDelete=1" -> {
                            realm.where(BuyerCustomProduct::class.java)
                                .equalTo(BuyerCustomProduct.COLUMN_ACTION_DELETED, 1L)
                                .findAll()
                        }
                        "actionCreated=1" -> {
                            realm.where(BuyerCustomProduct::class.java)
                                .equalTo(BuyerCustomProduct.COLUMN_ACTION_CREATED, 1L)
                                .findAll()
                        }
                        "actionEdited=1" -> {
                            realm.where(BuyerCustomProduct::class.java)
                                .equalTo(BuyerCustomProduct.COLUMN_ACTION_EDITED, 1L)
                                .findAll()
                        }
                        else -> null
                    }
                    if (message != null) {
                        val iterator = message.iterator()
                        while (iterator.hasNext()) {
                            itemId?.add(iterator.next().id ?: 0L)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("offline", "while fetching actions : " + e.message)
            } finally {
                realm.close()
            }
            return itemId
        }

        fun insertCustomProductsProduct(prod: List<OwnDesigns>) {
            nextID = 0L
            var arrProdImages=ArrayList<ProductImage>()
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    val iterator = prod.iterator()
                    while (iterator.hasNext()) {
                        val product=iterator.next()
                        val remoteProdId=product.id
                        var dbProdObj = realm.where(BuyerCustomProduct::class.java).equalTo(BuyerCustomProduct.COLUMN_ID, remoteProdId).limit(1).findFirst()
                        var uniqueId=try { dbProdObj!!.id?:0}catch (e: Exception){0}
                        if(uniqueId.equals(remoteProdId)) {

                            dbProdObj?.id = product.id
                            dbProdObj?.productCategoryId = product.productCategory.id
                            dbProdObj?.productCategoryDscrp = product.productCategory.productDesc
                            dbProdObj?.productTypeId = product.productType.id
                            dbProdObj?.warpYarnId = product.warpYarn.id
                            dbProdObj?.weftYarnId = product.weftYarn.id
                            dbProdObj?.extraWeftYarnId = product.extraWeftYarn.id
                            dbProdObj?.warpYarnCount=product.warpYarnCount
                            dbProdObj?.weftYarnCount=product.weftYarnCount
                            dbProdObj?.extraWeftYarnCount=product.extraWeftYarnCount

                            dbProdObj?.warpDyeId=product.warpDye.id
                            dbProdObj?.weftDyeId=product.weftDye.id
                            dbProdObj?.extraWeftDyeId=product.extraWeftDye.id
                            dbProdObj?.dyeDsrcp="${product.warpDye.dyeDesc} X ${product.weftDye.dyeDesc} X ${product.extraWeftDye.dyeDesc}"

                            dbProdObj?.length=product.length
                            dbProdObj?.width=product.width
                            dbProdObj?.reedCountId=product.reedCount.id
                            dbProdObj?.gsm=product.gsm
                            dbProdObj?.weight=product.weight
                            dbProdObj?.productSpe=product.product_spec
                            dbProdObj?.createdOn=product.createdOn
                            dbProdObj?.modifiedOn=product.modifiedOn
                            var relatedIds=""
                            product.relProduct?.forEach {relatedIds=relatedIds+it?.productTypeID+","  }
                            dbProdObj?.relatedProductId=relatedIds
                            dbProdObj?.buyerId=product.buyerId
                            product.productImages.forEach {
                                arrProdImages.add(it)
                            }
                            Log.e("ProdImage", "${product.productImages.size}")
                            realm?.copyToRealmOrUpdate(dbProdObj)
                        }
                        else{
                            var primId = it.where<BuyerCustomProduct>(BuyerCustomProduct::class.java)?.max(BuyerCustomProduct.COLUMN__ID)
                            nextID = if (primId == null) {
                                1
                            } else {
                                primId.toLong() + 1
                            }
                            var prodEntry = it.createObject(BuyerCustomProduct::class.java, nextID)
                            prodEntry.id = product.id
                            prodEntry.productCategoryId = product.productCategory.id
                            prodEntry.productCategoryDscrp = product.productCategory.productDesc
                            prodEntry.productTypeId = product.productType.id
                            prodEntry.warpYarnId = product.warpYarn.id
                            prodEntry.weftYarnId = product.weftYarn.id
                            prodEntry.extraWeftYarnId = product.extraWeftYarn.id
                            prodEntry.warpYarnCount=product.warpYarnCount
                            prodEntry.weftYarnCount=product.weftYarnCount
                            prodEntry.extraWeftYarnCount=product.extraWeftYarnCount

                            prodEntry.warpDyeId=product.warpDye.id
                            prodEntry.weftDyeId=product.weftDye.id
                            prodEntry.extraWeftDyeId=product.extraWeftDye.id
                            prodEntry.dyeDsrcp="${product.warpDye.dyeDesc} X ${product.weftDye.dyeDesc} X ${product.extraWeftDye.dyeDesc}"

                            prodEntry.length=product.length
                            prodEntry.width=product.width
                            prodEntry.reedCountId=product.reedCount.id
                            prodEntry.gsm=product.gsm
                            prodEntry.weight=product.weight
                            prodEntry.productSpe=product.product_spec
                            prodEntry.createdOn=product.createdOn
                            prodEntry.modifiedOn=product.modifiedOn
                            var relatedIds=""
                            product.relProduct?.forEach {relatedIds=relatedIds+it?.productTypeID+","  }
                            prodEntry.relatedProductId=relatedIds
                            prodEntry.buyerId=product.buyerId
                            product.productImages.forEach {
                                arrProdImages.add(it)
                            }
                            Log.e("ProdImage", "${product.productImages.size}")
                        }
                    }
                }
                if(arrProdImages.size>0)ProductImagePredicates.insertBuyerCustomProdImages(arrProdImages)

            } catch (e: Exception) {
                Log.e("ProductCatalogueLog", "$e")
            }
        }

        fun deleteAllOwnProducts(){
            val realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction {
                var results = realm.where(BuyerCustomProduct::class.java).findAll()
                results.deleteAllFromRealm()
            }
        }
        fun insertCustomProductOffline(
            template: OwnDesignRequest,
            imageList: ArrayList<String>,
            relatedProdList:ArrayList<RelatedProduct>
        ) {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
//                    var primId = it.where<BuyerCustomProduct>(BuyerCustomProduct::class.java)?.max(BuyerCustomProduct.COLUMN__ID)
//                    nextID = if (primId == null) {
//                        1
//                    } else {
//                        primId.toLong() + 1
//                    }
                    nextID=System.currentTimeMillis()
                    var prodEntry = it.createObject(BuyerCustomProduct::class.java, nextID)
                    prodEntry.id=nextID
                    prodEntry.actionCreated = 1
                    prodEntry.productCategoryId = template.productCategoryId
                    prodEntry.productSpe = template.productSpec
                    prodEntry.productTypeId = template.productTypeId

                    prodEntry.reedCountId = template.reedCountId.toLong()
                    prodEntry.gsm = template.gsm
                    prodEntry.width = template.width
                    prodEntry.length = template.length
                    prodEntry.weight = template.weight

                    prodEntry.warpDyeId = template.warpDyeId
                    prodEntry.warpYarnCount = template.warpYarnCount
                    prodEntry.warpYarnId = template.warpYarnId

                    prodEntry.weftDyeId = template.weftDyeId
                    prodEntry.weftYarnCount = template.weftYarnCount
                    prodEntry.weftYarnId = template.weftYarnId

                    prodEntry.extraWeftDyeId = template.extraWeftDyeId
                    prodEntry.extraWeftYarnCount = template.extraWeftYarnCount
                    prodEntry.extraWeftYarnId = template.extraWeftYarnId
                    Log.e("ArtisanProdLog","weave Ids ${template.weaveIds}")
                    prodEntry.weaveIds =template.weaveIds
                    //todo add related products, image paths,weave ids, was care instructions
                    Log.e("ArtisanProdLog","${prodEntry.weaveIds}")
                }

                if(relatedProdList.size>0)RelateProductPredicates.insertRelatedProduct(nextID,relatedProdList.get(0).productTypeID,relatedProdList.get(0).width,relatedProdList.get(0).length)
                ProductImagePredicates.insertProductImages(nextID,imageList)
            }catch (e:Exception){
                Log.e("ArtisanProdLog","${e.message}")
            }
        }

        fun getCustomProductData(): RealmResults<BuyerCustomProduct>? {
            val realm = CXRealmManager.getRealmInstance()
            var wishlistedItem: RealmResults<BuyerCustomProduct>? = null
            try {
                realm?.executeTransaction {
                    wishlistedItem = realm.where(BuyerCustomProduct::class.java)
                        .notEqualTo(BuyerCustomProduct.COLUMN_IS_DELETED,1L)
                        .and()
                        .notEqualTo(BuyerCustomProduct.COLUMN_ACTION_CREATED,1L)
                        .findAll()
                }
            } catch (e: Exception) {
                Log.e("addToWishlist", "${e}")
            } finally {
//            realm.close()
            }
            return wishlistedItem
        }

        fun updateProductForDeletion(id: Long?){
            var realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction{
                var product = realm.where(BuyerCustomProduct::class.java).equalTo(BuyerCustomProduct.COLUMN_ID,id).limit(1).findFirst()
                product?.isDeleted = 1
                product?.actionDelete = 1
            }
        }

        fun getAllCustomProducts(): RealmResults<BuyerCustomProduct>? {
            val realm = CXRealmManager.getRealmInstance()
            var wishlistedItem: RealmResults<BuyerCustomProduct>? = null
            try {
                realm?.executeTransaction {
                    wishlistedItem = realm.where(BuyerCustomProduct::class.java)
                        .findAll()
                }
            } catch (e: Exception) {
                Log.e("getAllCustomProducts", "${e}")
            } finally {
//            realm.close()
            }
            return wishlistedItem
        }

        fun deleteProductEntry(id:Long){
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                val artisonProd = it.where(BuyerCustomProduct::class.java).equalTo(BuyerCustomProduct.COLUMN_ID, id).findAll()
                artisonProd.deleteAllFromRealm()
            }

        }
    }
}