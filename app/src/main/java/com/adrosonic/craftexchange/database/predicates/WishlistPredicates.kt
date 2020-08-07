package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct.Data
import io.realm.RealmResults
import java.lang.Exception

class WishlistPredicates {

    companion object {
        private var nextID: Long? = 0

        fun actionWishlist(productId: Long?, actionWishlisting : Long?) {
            var realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction {
            var product = realm.where(ProductCatalogue::class.java)
                .equalTo(ProductCatalogue.COLUMN_PRODUCT_ID, productId).limit(1).findFirst()
            product?.actionWishlisted = actionWishlisting
            }
        }

        fun updateProductWishlisting(productId: Long?,isWishlisted : Long?,actionWishlisting : Long?){
            var realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction{
                var product = realm.where(ProductCatalogue::class.java).equalTo(ProductCatalogue.COLUMN_PRODUCT_ID,productId).limit(1).findFirst()
                product?.isWishlisted = isWishlisted
                product?.actionWishlisted = actionWishlisting
            }
        }

        fun getProductWishlisting(productId: Long?): ProductCatalogue? {
            var realm = CXRealmManager.getRealmInstance()
            var product : ProductCatalogue ? = null
            realm.executeTransaction{
                product = realm.where(ProductCatalogue::class.java).equalTo(ProductCatalogue.COLUMN_PRODUCT_ID,productId).limit(1).findFirst()
            }
            return product
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
                            itemId?.add(iterator.next().productId?:0L)
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

        fun insertSingleProduct(prod: Data){
            nextID = 0L
            var productId=0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var catalogueProduct =prod
                    var productObj = realm.where(ProductCatalogue::class.java)
                        .equalTo("productId",catalogueProduct.id)
                        .limit(1)
                        .findFirst()

                    if (productObj == null){
                        var primId = it.where(ProductCatalogue::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var exprod = it.createObject(ProductCatalogue::class.java,
                            nextID
                        )
                        exprod.artisanId = catalogueProduct?.artitionID
                        exprod.artisanName = catalogueProduct?.artistName
                        exprod.clusterId = catalogueProduct?.clusterID
                        exprod.clusterName = catalogueProduct?.clusterName
                        exprod.brandName = catalogueProduct?.brand
                        productId=catalogueProduct?.id
                        exprod.productId = catalogueProduct?.id
                        exprod.productCode = catalogueProduct?.code
                        exprod.productTag = catalogueProduct?.tag

                        exprod.productCategoryId = catalogueProduct?.productCategory?.id
                        exprod.productCategoryName = catalogueProduct?.productCategory?.productDesc
                        exprod.productCategoryCode = catalogueProduct?.productCategory?.code

                        exprod.productTypeId = catalogueProduct?.productType?.id
                        exprod.productTypeDesc = catalogueProduct?.productType?.productDesc
                        exprod.inProductCategory = catalogueProduct?.productType?.productCategoryID

                        exprod.warpYarnId = catalogueProduct?.warpYarn?.id
                        exprod.warpYarnDesc = catalogueProduct?.warpYarn?.yarnDesc
                        exprod.warpYarnCount = catalogueProduct?.warpYarnCount
//                                exprod.warpYarnTypeId = catalogueProduct?.warpYarn?.yarnType?.id TODO : to be used later if required
                        exprod.warpDyeId = catalogueProduct?.warpYarn?.id
                        exprod.warpDyeId = catalogueProduct?.warpDye?.id
                        exprod.warpDyeDesc = catalogueProduct?.warpDye?.dyeDesc

                        exprod.weftYarnId = catalogueProduct?.weftYarn?.id
                        exprod.weftYarnDesc = catalogueProduct?.weftYarn?.yarnDesc
                        exprod.weftYarnCount = catalogueProduct?.weftYarnCount
//                                exprod.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.weftDyeId = catalogueProduct?.weftDye?.id
                        exprod.weftDyeDesc = catalogueProduct?.weftDye?.dyeDesc

                        exprod.extraWeftYarnId = catalogueProduct?.extraWeftYarn?.id
                        exprod.extraWeftYarnDesc = catalogueProduct?.extraWeftYarn?.yarnDesc
                        exprod.extraWeftYarnCount = catalogueProduct?.extraWeftYarnCount
//                                exprod.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.extraWeftDyeId = catalogueProduct?.extraWeftDye?.id
                        exprod.extraWeftDyeDesc = catalogueProduct?.extraWeftDye?.dyeDesc

                        exprod.productLength = catalogueProduct?.length
                        exprod.productWidth = catalogueProduct?.width

                        exprod.reedCountId = catalogueProduct?.reedCount?.id
                        exprod.reedCount = catalogueProduct?.reedCount?.count

                        exprod.productStatusId = catalogueProduct?.productStatusId

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        exprod.gsm = catalogueProduct?.gsm
                        exprod.weight = catalogueProduct?.weight
                        exprod.product_spe = catalogueProduct?.product_spe

                        exprod.createdOn = catalogueProduct?.createdOn
                        exprod.modifiedOn = catalogueProduct?.modifiedOn
                        exprod.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        exprod.isDeleted = catalogueProduct?.isDeleted

                        realm.copyToRealmOrUpdate(exprod)
                    }
                    else{
                        nextID = productObj._id ?: 0
                        productObj.artisanId = catalogueProduct?.artitionID
                        productObj.artisanName = catalogueProduct?.artistName
                        productObj.clusterId = catalogueProduct?.clusterID
                        productObj.clusterName = catalogueProduct?.clusterName
                        productObj.brandName = catalogueProduct?.brand

                        productObj.productId = catalogueProduct?.id
                        productObj.productCode = catalogueProduct?.code
                        productObj.productTag = catalogueProduct?.tag

                        productObj.productCategoryId = catalogueProduct?.productCategory?.id
                        productObj.productCategoryName = catalogueProduct?.productCategory?.productDesc
                        productObj.productCategoryCode = catalogueProduct?.productCategory?.code

                        productObj.productTypeId = catalogueProduct?.productType?.id
                        productObj.productTypeDesc = catalogueProduct?.productType?.productDesc
                        productObj.inProductCategory = catalogueProduct?.productType?.productCategoryID

                        productObj.warpYarnId = catalogueProduct?.warpYarn?.id
                        productObj.warpYarnDesc = catalogueProduct?.warpYarn?.yarnDesc
                        productObj.warpYarnCount = catalogueProduct?.warpYarnCount
//                                productObj.warpYarnTypeId = catalogueProduct?.warpYarn?.yarnType?.id TODO : to be used later if required
                        productObj.warpDyeId = catalogueProduct?.warpYarn?.id
                        productObj.warpDyeId = catalogueProduct?.warpDye?.id
                        productObj.warpDyeDesc = catalogueProduct?.warpDye?.dyeDesc

                        productObj.weftYarnId = catalogueProduct?.weftYarn?.id
                        productObj.weftYarnDesc = catalogueProduct?.weftYarn?.yarnDesc
                        productObj.weftYarnCount = catalogueProduct?.weftYarnCount
//                                productObj.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.weftDyeId = catalogueProduct?.weftDye?.id
                        productObj.weftDyeDesc = catalogueProduct?.weftDye?.dyeDesc

                        productObj.extraWeftYarnId = catalogueProduct?.extraWeftYarn?.id
                        productObj.extraWeftYarnDesc = catalogueProduct?.extraWeftYarn?.yarnDesc
                        productObj.extraWeftYarnCount = catalogueProduct?.extraWeftYarnCount
//                                productObj.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.extraWeftDyeId = catalogueProduct?.extraWeftDye?.id
                        productObj.extraWeftDyeDesc = catalogueProduct?.extraWeftDye?.dyeDesc

                        productObj.productLength = catalogueProduct?.length
                        productObj.productWidth = catalogueProduct?.width

                        productObj.reedCountId = catalogueProduct?.reedCount?.id
                        productObj.reedCount = catalogueProduct?.reedCount?.count

                        productObj.productStatusId = catalogueProduct?.productStatusId

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        productObj.gsm = catalogueProduct?.gsm
                        productObj.weight = catalogueProduct?.weight
                        productObj.product_spe = catalogueProduct?.product_spe

                        productObj.createdOn = catalogueProduct?.createdOn
                        productObj.modifiedOn = catalogueProduct?.modifiedOn
                        productObj.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        productObj.isDeleted = catalogueProduct?.isDeleted

                        realm.copyToRealmOrUpdate(productObj)
                    }

                    var imageList = catalogueProduct?.productImages
                    var imageItr = imageList?.iterator()
                    if(imageItr!=null){
                        while (imageItr.hasNext()){
                            var image = imageItr.next()

                            var imageObj = realm.where(ProductImages::class.java)
                                .equalTo("imageId",image.id)
                                .limit(1)
                                .findFirst()
                            if(imageObj == null){
                                var primId = it.where(ProductImages::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var eximg = it.createObject(
                                    ProductImages::class.java,
                                    nextID
                                )
                                eximg.productId = productId
                                eximg.imageId = image?.id
                                eximg.imageName = image?.lable
                                realm.copyToRealmOrUpdate(eximg)
                            }else{
                                nextID = imageObj?._id ?: 0
                                imageObj?.productId =productId
                                imageObj?.imageId = image?.id
                                imageObj?.imageName = image?.lable
                                realm.copyToRealmOrUpdate(imageObj)
                            }

                        }
                    }

                    var relatedProductList = catalogueProduct?.relProduct
                    var prodItr = relatedProductList?.iterator()
                    if(prodItr!=null){
                        while (prodItr.hasNext()){
                            var relProduct = prodItr.next()

                            var relPoprductObj = realm.where(RelatedProducts::class.java)
                                .equalTo("relatedProductId",relProduct.id)
                                .limit(1)
                                .findFirst()
                            if(relPoprductObj == null){
                                var primId = it.where(RelatedProducts::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exRelProd= it.createObject(
                                    RelatedProducts::class.java,
                                    nextID
                                )
                                exRelProd?.inProductCategoryId = catalogueProduct?.productCategory?.id
                                exRelProd?.relatedToProductId = catalogueProduct?.id
                                exRelProd?.relatedProductId = relProduct?.id
                                exRelProd?.productTypeId = relProduct?.productType?.id
                                exRelProd?.productName = relProduct?.productType?.productDesc
                                exRelProd?.productLength = relProduct?.length
                                exRelProd?.productWidth = relProduct?.width
                                exRelProd?.productWeight = relProduct?.weight

                                realm?.copyToRealmOrUpdate(exRelProd)
                            }else{
                                relPoprductObj?.inProductCategoryId = catalogueProduct?.productCategory?.id
                                relPoprductObj?.relatedToProductId = catalogueProduct?.id
                                relPoprductObj?.relatedProductId = relProduct?.id
                                relPoprductObj?.productTypeId = relProduct?.productType?.id
                                relPoprductObj?.productName = relProduct?.productType?.productDesc
                                relPoprductObj?.productLength = relProduct?.length
                                relPoprductObj?.productWidth = relProduct?.width
                                relPoprductObj?.productWeight = relProduct?.weight

                                realm.copyToRealmOrUpdate(relPoprductObj)
                            }
                        }
                    }

                    var weaveTypeList = catalogueProduct?.productWeaves
                    var weaveTypeItr = weaveTypeList?.iterator()
                    if(weaveTypeItr!=null){
                        while (weaveTypeItr.hasNext()){
                            var weaveType = weaveTypeItr.next()

                            var weaveTypeObj = realm.where(WeaveTypes::class.java)
                                .equalTo("productId",weaveType.productID)
                                .and()
                                .equalTo("weaveId",weaveType.weaveID)
                                .limit(1)
                                .findFirst()
                            if(weaveTypeObj == null){
                                var primId = it.where(WeaveTypes::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exWeaveType = it.createObject(
                                    WeaveTypes::class.java,
                                    nextID
                                )
                                exWeaveType?.productId = weaveType?.productID
                                exWeaveType?.productWeaveId = weaveType?.id
                                exWeaveType?.weaveId = weaveType?.productID
                                realm.copyToRealmOrUpdate(exWeaveType)
                            }else{
                                nextID = weaveTypeObj?._id ?: 0
                                weaveTypeObj?.productId = weaveType?.productID
                                weaveTypeObj?.productWeaveId = weaveType?.id
                                weaveTypeObj?.weaveId = weaveType?.productID
                                realm.copyToRealmOrUpdate(weaveTypeObj)
                            }

                        }
                    }

                    var careList = catalogueProduct?.productCares
                    var careItr = careList?.iterator()
                    if(careItr!=null){
                        while (careItr.hasNext()){
                            var care = careItr.next()

                            var careObj = realm.where(ProductCares::class.java)
                                .equalTo("productId",care.productID)
                                .and()
                                .equalTo("productCareId",care.productCareID)
                                .limit(1)
                                .findFirst()
                            if(careObj == null){
                                var primId = it.where(ProductCares::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exCare = it.createObject(
                                    ProductCares::class.java,
                                    nextID
                                )
                                exCare?.productId = care?.productID
                                exCare?.careId = care?.id
                                exCare?.productCareId = care?.productCareID
                                realm.copyToRealmOrUpdate(exCare)
                            }else{
                                nextID = careObj?._id ?: 0
                                careObj?.productId = care?.productID
                                careObj?.careId = care?.id
                                careObj?.productCareId = care?.productCareID
                                realm.copyToRealmOrUpdate(careObj)
                            }

                        }
                    }
                }

            }catch (e:Exception){
                Log.e("ProductCatalogueLog","$e")
            }
        }

        fun addToWishlist(wishListedProd:List<Long>?){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm?.executeTransaction {
                    var productCatalog = realm.where(ProductCatalogue::class.java).findAll()
                    for (product in productCatalog) {
                        if(wishListedProd?.contains(product?.productId!!)!!){
                            product.isWishlisted=1
                        }else product.isWishlisted=0
                    }
                    realm.copyToRealmOrUpdate(productCatalog)
                }
            } catch (e: Exception) {
                Log.e("addToWishlist","${e}")
            } finally {
//            realm.close()
            }
        }

        fun isProductPresent(id:Long):Boolean{
            var flag=false
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm?.executeTransaction {
                    val id=id
                    Log.e("addToWishlist","isProductPresent :$id")
                    var productCatalog = realm.where(ProductCatalogue::class.java)
                        .equalTo(ProductCatalogue.COLUMN_PRODUCT_ID,id).limit(1)
                        .findFirst()
                    if(productCatalog!=null)   flag= true
                    else flag= false
                }
            } catch (e: Exception) {
                Log.e("isProductPresent","${e}")
                flag= false
            } finally {
//            realm.close()
            }
            return flag
        }

        fun getWishListedData(): RealmResults<ProductCatalogue>? {
            val realm = CXRealmManager.getRealmInstance()
            var wishlistedItem: RealmResults<ProductCatalogue>?=null
            try {
                realm?.executeTransaction {
                    wishlistedItem=realm.where(ProductCatalogue::class.java)
                        .equalTo(ProductCatalogue.COLUMN_IS_WISHLISTED,1L)
                        .findAll()
                }
            } catch (e: Exception) {
                Log.e("addToWishlist","${e}")
            } finally {
//            realm.close()
            }
            return wishlistedItem
        }
    }
}