package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.database.entities.realmEntities.brandProducts.BrandList
import com.adrosonic.craftexchange.database.entities.realmEntities.brandProducts.BrandProducts
import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.brand.BrandProductDetailResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.cluster.ClusterProductDetailResponse
import io.realm.RealmResults
import java.lang.Exception

class ProductPredicates {
    companion object {
        private var nextID: Long? = 0

        //TODO : INSERT

        fun insertArtisanProductCategory(user: ProfileResponse?) : Long? {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var productList = user?.data?.userProductCategories
            try {
                realm?.executeTransaction {
                    var prodIterator = productList?.iterator()
                    if (prodIterator != null) {
                        while (prodIterator.hasNext()) {
                            var prod = prodIterator.next()
                            var prodObj = realm.where(ArtisanProductCategory::class.java)
                                .equalTo("id", prod.id)
                                .limit(1)
                                .findFirst()

                            if (prodObj == null) {
                                var primId = it.where(ArtisanProductCategory::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exprod = it.createObject(ArtisanProductCategory::class.java, nextID)
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

        fun insertAllCategoryProducts(prod : AllProductsResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var catProd = prod?.data
            try {
                realm.executeTransaction {
                    var catItr = catProd?.iterator()
                    if (catItr != null) {
                        while (catItr.hasNext()){
                            var product = catItr.next()

                            var catProdType = product.productTypes
                            var prdtypeItr = catProdType?.iterator()
                            if(prdtypeItr !=null){
                                while (prdtypeItr.hasNext()) {
                                    var catProd = prdtypeItr.next()

                                    //Adding products in sub category
                                    var productObj = realm.where(CategoryProducts::class.java)
                                        .equalTo("subProductid",catProd.id)
                                        .limit(1)
                                        .findFirst()

                                    if(productObj == null){
                                        var primId = it.where(CategoryProducts::class.java).max("_id")
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var exprod = it.createObject(CategoryProducts::class.java, nextID)
                                        exprod.productid = product.id
                                        exprod.code = product.code
                                        exprod.product = product.productDesc
                                        exprod.subProductid = catProd.id
                                        exprod.subProduct = catProd.productDesc
                                        exprod.prodCategoryid = catProd.productCategoryId


                                        realm.copyToRealmOrUpdate(exprod)
                                    }else{
                                        nextID = productObj._id ?: 0

                                        productObj.productid = product.id
                                        productObj.code = product.code
                                        productObj.product = product.productDesc
                                        productObj.subProductid = catProd.id
                                        productObj.subProduct = catProd.productDesc
                                        productObj.prodCategoryid = catProd.productCategoryId

                                        realm.copyToRealmOrUpdate(productObj)

                                    }

                                    var length = catProd.productLengths
                                    var width = catProd.productWidths

                                    var lengthItr = length.iterator()
                                    var widthItr = width.iterator()

                                    while (lengthItr.hasNext()){
                                        var pLength = lengthItr.next()
                                        var lengthObj = realm.where(ProductDimens::class.java)
                                            .equalTo("lengthId",pLength.id)
                                            .limit(1)
                                            .findFirst()

                                        if(lengthObj == null){
                                            var primId = it.where(ProductDimens::class.java).max("_id")
                                            if (primId == null) {
                                                nextID = 1
                                            } else {
                                                nextID = primId.toLong() + 1
                                            }
                                            var exdimen = it.createObject(ProductDimens::class.java, nextID)
                                            exdimen.inProductCategory = product.id
                                            exdimen.prodTypeId = catProd.id
                                            exdimen.productType = catProd.productDesc
                                            exdimen.lengthId = pLength.id
                                            exdimen.length = pLength.length

                                            realm.copyToRealmOrUpdate(exdimen)
                                        }else{
                                            nextID = lengthObj._id ?: 0
                                            lengthObj.inProductCategory = product.id
                                            lengthObj.prodTypeId = catProd.id
                                            lengthObj.productType = catProd.productDesc
                                            lengthObj.lengthId = pLength.id
                                            lengthObj.length = pLength.length

                                            realm.copyToRealmOrUpdate(lengthObj)
                                        }
                                    }

                                    while (widthItr.hasNext()){
                                        var pWidth = widthItr.next()
                                        var widthObj = realm.where(ProductDimens::class.java)
                                            .equalTo("prodTypeId",pWidth.productTypeId)
                                            .limit(1)
                                            .findFirst()

                                        if(widthObj == null){
                                            var primId = it.where(ProductDimens::class.java).max("_id")
                                            if (primId == null) {
                                                nextID = 1
                                            } else {
                                                nextID = primId.toLong() + 1
                                            }
                                            var exdimen = it.createObject(ProductDimens::class.java, nextID)
                                            exdimen.prodTypeId = catProd.id
                                            exdimen.productType = catProd.productDesc
                                            exdimen.widthId = pWidth.id
                                            exdimen.width = pWidth.width

                                            realm.copyToRealmOrUpdate(exdimen)
                                        }else{
//                                        nextID = lengthObj._id ?: 0
//                                        lengthObj.prodTypeId = catProd.id
//                                        lengthObj.productType = catProd.productDesc
                                            widthObj.widthId = pWidth.id
                                            widthObj.width = pWidth.width

                                            realm.copyToRealmOrUpdate(widthObj)
                                        }
                                    }
                                }
                            }
                        }
//
                    }
                }
            }catch (e:Exception){
                Log.e("catProdLog","$e")
            }
        }

        fun insertBrands(list : BrandListResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var brandProd = list?.data
            try {
                realm.executeTransaction {
                    var brandItr = brandProd?.iterator()
                    if (brandItr != null) {
                        while (brandItr.hasNext()){
                            var brand = brandItr.next()
                            var brandObj = realm.where(BrandList::class.java)
                                .equalTo("artisanId", brand.artisanId)
                                .limit(1)
                                .findFirst()

                            if (brandObj == null) {
                                var primId = it.where(BrandList::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exbrand = it.createObject(BrandList::class.java, nextID)
                                exbrand.artisanId = brand.artisanId
                                exbrand.clusterId = brand.clusterId
                                exbrand.firstName = brand.firstName
                                exbrand.companyName = brand.companyName
                                exbrand.profilePic = brand.profilePic
                                exbrand.logo = brand.logo
//                                exbrand.productImages = brand.productImages //TODO : to be implemented later
                                realm.copyToRealmOrUpdate(exbrand)
                            } else {
                                nextID = brandObj._id ?: 0
                                brandObj.artisanId = brand.artisanId
                                brandObj.clusterId = brand.clusterId
                                brandObj.firstName = brand.firstName
                                brandObj.companyName = brand.companyName
                                brandObj.profilePic = brand.profilePic
                                brandObj.logo = brand.logo
//                                brandObj.productImages = brand.productImages  //TODO : to be implemented later
                                realm.copyToRealmOrUpdate(brandObj)
                            }

                        }

                    }
                }
            }catch (e:Exception){

            }
        }

        fun insertBrandProducts(productList : BrandProductDetailResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var product = productList?.data?.products
            try {
                realm.executeTransaction {
                    var prodItr = product?.iterator()
                    if (prodItr != null) {
                        while (prodItr.hasNext()){
                            var brandProduct = prodItr.next()
                            var productObj = realm.where(BrandProducts::class.java)
                                .equalTo("productId",brandProduct.id)
                                .limit(1)
                                .findFirst()

                            if (productObj == null){
                                var primId = it.where(BrandProducts::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exprod = it.createObject(BrandProducts::class.java, nextID)
                                exprod.artisanId = brandProduct?.artitionId
                                exprod.artisanName = brandProduct?.artistName
                                exprod.clusterId = brandProduct?.clusterId
                                exprod.clusterName = brandProduct?.clusterName
                                exprod.brandName = brandProduct?.brand

                                exprod.productId = brandProduct?.id
                                exprod.productCode = brandProduct?.code
                                exprod.productTag = brandProduct?.tag

                                exprod.productCategoryId = brandProduct?.productCategory?.id
                                exprod.productCategoryName = brandProduct?.productCategory?.productDesc
                                exprod.productCategoryCode = brandProduct?.productCategory?.code

                                exprod.productTypeId = brandProduct?.productType?.id
                                exprod.productTypeDesc = brandProduct?.productType?.productDesc
                                exprod.inProductCategory = brandProduct?.productType?.productCategoryId

                                exprod.warpYarnId = brandProduct?.warpYarn?.id
                                exprod.warpYarnDesc = brandProduct?.warpYarn?.yarnDesc
                                exprod.warpYarnTypeId = brandProduct?.warpYarn?.yarnType?.id
                                exprod.warpDyeId = brandProduct?.warpYarn?.id
                                exprod.warpDyeId = brandProduct?.warpDye?.id
                                exprod.warpDyeDesc = brandProduct?.warpDye?.dyeDesc

                                exprod.weftYarnId = brandProduct?.weftYarn?.id
                                exprod.weftYarnDesc = brandProduct?.weftYarn?.yarnDesc
                                exprod.weftYarnTypeId = brandProduct?.weftYarn?.yarnType?.id
                                exprod.weftDyeId = brandProduct?.weftDye?.id
                                exprod.weftDyeDesc = brandProduct?.weftDye?.dyeDesc

                                exprod.extraWeftYarnId = brandProduct?.extraWeftYarn?.id
                                exprod.extraWeftYarnDesc = brandProduct?.extraWeftYarn?.yarnDesc
                                exprod.extraWeftYarnTypeId = brandProduct?.extraWeftYarn?.yarnType?.id
                                exprod.extraWeftDyeId = brandProduct?.extraWeftDye?.id
                                exprod.extraWeftDyeDesc = brandProduct?.extraWeftDye?.dyeDesc

                                exprod.productLength = brandProduct?.length
                                exprod.productWidth = brandProduct?.width

                                exprod.reedCountId = brandProduct?.reedCount?.id
                                exprod.reedCount = brandProduct?.reedCount?.count

                                exprod.productStatusId = brandProduct?.productStatusId

                                //TODO : ProductCares, ProductImages and ProductWeaves...different table
                                //TODO : Related Product Types to be implemented later

                                exprod.gsm = brandProduct?.gsm
                                exprod.weight = brandProduct?.weight
                                exprod.product_spe = brandProduct?.product_spe

                                exprod.createdOn = brandProduct?.createdOn
                                exprod.modifiedOn = brandProduct?.modifiedOn
                                exprod.madeWithAntaran = brandProduct?.madeWithAnthran
                                exprod.isDeleted = brandProduct?.isDeleted

                                realm.copyToRealmOrUpdate(exprod)
                            }else{
                                nextID = productObj._id ?: 0
                                productObj.artisanId = brandProduct?.artitionId
                                productObj.artisanName = brandProduct?.artistName
                                productObj.clusterId = brandProduct?.clusterId
                                productObj.clusterName = brandProduct?.clusterName
                                productObj.brandName = brandProduct?.brand

                                productObj.productId = brandProduct?.id
                                productObj.productCode = brandProduct?.code
                                productObj.productTag = brandProduct?.tag

                                productObj.productCategoryId = brandProduct?.productCategory?.id
                                productObj.productCategoryName = brandProduct?.productCategory?.productDesc
                                productObj.productCategoryCode = brandProduct?.productCategory?.code

                                productObj.productTypeId = brandProduct?.productType?.id
                                productObj.productTypeDesc = brandProduct?.productType?.productDesc
                                productObj.inProductCategory = brandProduct?.productType?.productCategoryId

                                productObj.warpYarnId = brandProduct?.warpYarn?.id
                                productObj.warpYarnDesc = brandProduct?.warpYarn?.yarnDesc
                                productObj.warpYarnTypeId = brandProduct?.warpYarn?.yarnType?.id
                                productObj.warpDyeId = brandProduct?.warpYarn.id
                                productObj.warpDyeId = brandProduct?.warpDye?.id
                                productObj.warpDyeDesc = brandProduct?.warpDye?.dyeDesc

                                productObj.weftYarnId = brandProduct?.weftYarn?.id
                                productObj.weftYarnDesc = brandProduct?.weftYarn?.yarnDesc
                                productObj.weftYarnTypeId = brandProduct?.weftYarn?.yarnType?.id
                                productObj.weftDyeId = brandProduct?.weftDye?.id
                                productObj.weftDyeDesc = brandProduct?.weftDye?.dyeDesc

                                productObj.extraWeftYarnId = brandProduct?.extraWeftYarn?.id
                                productObj.extraWeftYarnDesc = brandProduct?.extraWeftYarn?.yarnDesc
                                productObj.extraWeftYarnTypeId = brandProduct?.extraWeftYarn?.yarnType?.id
                                productObj.extraWeftDyeId = brandProduct?.extraWeftDye?.id
                                productObj.extraWeftDyeDesc = brandProduct?.extraWeftDye?.dyeDesc

                                productObj.productLength = brandProduct?.length
                                productObj.productWidth = brandProduct?.width

                                productObj.reedCountId = brandProduct?.reedCount?.id
                                productObj.reedCount = brandProduct?.reedCount?.count

                                productObj.productStatusId = brandProduct?.productStatusId

                                //TODO : ProductCares, ProductImages and ProductWeaves...different table
                                //TODO : Related Product Types to be implemented later

                                productObj.gsm = brandProduct?.gsm
                                productObj.weight = brandProduct?.weight
                                productObj.product_spe = brandProduct?.product_spe

                                productObj.createdOn = brandProduct?.createdOn
                                productObj.modifiedOn = brandProduct?.modifiedOn
                                productObj.madeWithAntaran = brandProduct?.madeWithAnthran
                                productObj.isDeleted = brandProduct?.isDeleted

                                realm.copyToRealmOrUpdate(productObj)
                            }
                        }
                    }
                }
            }catch (e:Exception){
                Log.e("brandProdLog","$e")
            }
        }

        fun insertClusterProducts(productList : ClusterProductDetailResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var product = productList?.data?.products
            try {
                realm.executeTransaction {
                    var prodItr = product?.iterator()
                    if (prodItr != null) {
                        while (prodItr.hasNext()){
                            var clusterProduct = prodItr.next()
                            var productObj = realm.where(ClusterProducts::class.java)
                                .equalTo("productId",clusterProduct.id)
                                .limit(1)
                                .findFirst()

                            if (productObj == null){
                                var primId = it.where(ClusterProducts::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exprod = it.createObject(ClusterProducts::class.java, nextID)
                                exprod.artisanId = clusterProduct?.artitionId
                                exprod.artisanName = clusterProduct?.artistName
                                exprod.clusterId = clusterProduct?.clusterId
                                exprod.clusterName = clusterProduct?.clusterName
                                exprod.brandName = clusterProduct?.brand

                                exprod.productId = clusterProduct?.id
                                exprod.productCode = clusterProduct?.code
                                exprod.productTag = clusterProduct?.tag

                                exprod.productCategoryId = clusterProduct?.productCategory?.id
                                exprod.productCategoryName = clusterProduct?.productCategory?.productDesc
                                exprod.productCategoryCode = clusterProduct?.productCategory?.code

                                exprod.productTypeId = clusterProduct?.productType?.id
                                exprod.productTypeDesc = clusterProduct?.productType?.productDesc
                                exprod.inProductCategory = clusterProduct?.productType?.productCategoryId

                                exprod.warpYarnId = clusterProduct?.warpYarn?.id
                                exprod.warpYarnDesc = clusterProduct?.warpYarn?.yarnDesc
                                exprod.warpYarnTypeId = clusterProduct?.warpYarn?.yarnType?.id
                                exprod.warpDyeId = clusterProduct?.warpYarn?.id
                                exprod.warpDyeId = clusterProduct?.warpDye?.id
                                exprod.warpDyeDesc = clusterProduct?.warpDye?.dyeDesc

                                exprod.weftYarnId = clusterProduct?.weftYarn?.id
                                exprod.weftYarnDesc = clusterProduct?.weftYarn?.yarnDesc
                                exprod.weftYarnTypeId = clusterProduct?.weftYarn?.yarnType?.id
                                exprod.weftDyeId = clusterProduct?.weftDye?.id
                                exprod.weftDyeDesc = clusterProduct?.weftDye?.dyeDesc

                                exprod.extraWeftYarnId = clusterProduct?.extraWeftYarn?.id
                                exprod.extraWeftYarnDesc = clusterProduct?.extraWeftYarn?.yarnDesc
                                exprod.extraWeftYarnTypeId = clusterProduct?.extraWeftYarn?.yarnType?.id
                                exprod.extraWeftDyeId = clusterProduct?.extraWeftDye?.id
                                exprod.extraWeftDyeDesc = clusterProduct?.extraWeftDye?.dyeDesc

                                exprod.productLength = clusterProduct?.length
                                exprod.productWidth = clusterProduct?.width

                                exprod.reedCountId = clusterProduct?.reedCount?.id
                                exprod.reedCount = clusterProduct?.reedCount?.count

                                exprod.productStatusId = clusterProduct?.productStatusId

                                //TODO : ProductCares, ProductImages and ProductWeaves...different table
                                //TODO : Related Product Types to be implemented later

                                exprod.gsm = clusterProduct?.gsm
                                exprod.weight = clusterProduct?.weight
                                exprod.product_spe = clusterProduct?.product_spe

                                exprod.createdOn = clusterProduct?.createdOn
                                exprod.modifiedOn = clusterProduct?.modifiedOn
                                exprod.madeWithAntaran = clusterProduct?.madeWithAnthran
                                exprod.isDeleted = clusterProduct?.isDeleted

                                realm.copyToRealmOrUpdate(exprod)
                            }else{
                                nextID = productObj._id ?: 0
                                productObj.artisanId = clusterProduct?.artitionId
                                productObj.artisanName = clusterProduct?.artistName
                                productObj.clusterId = clusterProduct?.clusterId
                                productObj.clusterName = clusterProduct?.clusterName
                                productObj.brandName = clusterProduct?.brand

                                productObj.productId = clusterProduct?.id
                                productObj.productCode = clusterProduct?.code
                                productObj.productTag = clusterProduct?.tag

                                productObj.productCategoryId = clusterProduct?.productCategory?.id
                                productObj.productCategoryName = clusterProduct?.productCategory?.productDesc
                                productObj.productCategoryCode = clusterProduct?.productCategory?.code

                                productObj.productTypeId = clusterProduct?.productType?.id
                                productObj.productTypeDesc = clusterProduct?.productType?.productDesc
                                productObj.inProductCategory = clusterProduct?.productType?.productCategoryId

                                productObj.warpYarnId = clusterProduct?.warpYarn?.id
                                productObj.warpYarnDesc = clusterProduct?.warpYarn?.yarnDesc
                                productObj.warpYarnTypeId = clusterProduct?.warpYarn?.yarnType?.id
                                productObj.warpDyeId = clusterProduct?.warpYarn?.id
                                productObj.warpDyeId = clusterProduct?.warpDye?.id
                                productObj.warpDyeDesc = clusterProduct?.warpDye?.dyeDesc

                                productObj.weftYarnId = clusterProduct?.weftYarn?.id
                                productObj.weftYarnDesc = clusterProduct?.weftYarn?.yarnDesc
                                productObj.weftYarnTypeId = clusterProduct?.weftYarn?.yarnType?.id
                                productObj.weftDyeId = clusterProduct?.weftDye?.id
                                productObj.weftDyeDesc = clusterProduct?.weftDye?.dyeDesc

                                productObj.extraWeftYarnId = clusterProduct?.extraWeftYarn?.id
                                productObj.extraWeftYarnDesc = clusterProduct?.extraWeftYarn?.yarnDesc
                                productObj.extraWeftYarnTypeId = clusterProduct?.extraWeftYarn?.yarnType?.id
                                productObj.extraWeftDyeId = clusterProduct?.extraWeftDye?.id
                                productObj.extraWeftDyeDesc = clusterProduct?.extraWeftDye?.dyeDesc

                                productObj.productLength = clusterProduct?.length
                                productObj.productWidth = clusterProduct?.width

                                productObj.reedCountId = clusterProduct?.reedCount?.id
                                productObj.reedCount = clusterProduct?.reedCount?.count

                                productObj.productStatusId = clusterProduct?.productStatusId

                                //TODO : ProductCares, ProductImages and ProductWeaves...different table
                                //TODO : Related Product Types to be implemented later

                                productObj.gsm = clusterProduct?.gsm
                                productObj.weight = clusterProduct?.weight
                                productObj.product_spe = clusterProduct?.product_spe

                                productObj.createdOn = clusterProduct?.createdOn
                                productObj.modifiedOn = clusterProduct?.modifiedOn
                                productObj.madeWithAntaran = clusterProduct?.madeWithAnthran
                                productObj.isDeleted = clusterProduct?.isDeleted

                                realm.copyToRealmOrUpdate(productObj)
                            }
                        }
                    }
                }
            }catch (e:Exception){
                Log.e("clusterProdLog","$e")
            }
        }

        fun insertProductsOfArtisan(productList : ArtisanProductDetailsResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var data = productList?.data
            var size = data?.size
            try {
                realm.executeTransaction {
                    var dataItr = data?.iterator()
                    if (dataItr != null) {
                        while (dataItr.hasNext()){
                            var data = dataItr?.next()

                            var prodlist = data?.products
                            var prodItr = prodlist?.iterator()
                            if(prodItr != null){
                                while (prodItr.hasNext()){
                                    var brandProduct = prodItr?.next()

                                    var productObj = realm.where(ArtisanProducts::class.java)
                                        .equalTo("productId",brandProduct.id)
                                        .limit(1)
                                        .findFirst()

                                    if (productObj == null){
                                        var primId = it.where(ArtisanProducts::class.java).max("_id")
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var exprod = it.createObject(ArtisanProducts::class.java, nextID)
                                        exprod.artisanId = brandProduct?.artitionId
//                                    exprod.artisanName = brandProduct?.artistName
//                                    exprod.clusterId = brandProduct?.clusterId
                                        exprod.clusterName = brandProduct?.clusterName
//                                    exprod.brandName = brandProduct?.brand

                                        exprod.productId = brandProduct?.id
                                        exprod.productCode = brandProduct?.code
                                        exprod.productTag = brandProduct?.tag

                                        exprod.productCategoryId = brandProduct?.productCategoryId
                                        exprod.productCategoryDesc = brandProduct?.productCategoryDesc
//                                    exprod.productCategoryCode = brandProduct?.productCategory?.code

                                        exprod.productTypeId = brandProduct?.productTypeId
                                        exprod.productTypeDesc = brandProduct?.productTypeDesc
//                                    exprod.inProductCategory = brandProduct?.productType?.productCategoryId

                                        exprod.warpYarnId = brandProduct?.warpYarnId
//                                    exprod.warpYarnDesc = brandProduct?.warpYarn?.yarnDesc
//                                    exprod.warpYarnTypeId = brandProduct?.warpYarn?.yarnType?.id
//                                    exprod.warpDyeId = brandProduct?.warpYarn?.id
                                        exprod.warpDyeId = brandProduct?.warpDyeId
//                                    exprod.warpDyeDesc = brandProduct?.warpDye?.dyeDesc
                                        exprod.warpYarnCount = brandProduct?.warpYarnCount

                                        exprod.weftYarnId = brandProduct?.weftYarnId
//                                    exprod.weftYarnDesc = brandProduct?.weftYarn?.yarnDesc
//                                    exprod.weftYarnTypeId = brandProduct?.weftYarn?.yarnType?.id
                                        exprod.weftDyeId = brandProduct?.weftDyeId
//                                    exprod.weftDyeDesc = brandProduct?.weftDye?.dyeDesc
                                        exprod.weftYarnCount = brandProduct?.weftYarnCount


                                        exprod.extraWeftYarnId = brandProduct?.extraWeftYarnId
//                                    exprod.extraWeftYarnDesc = brandProduct?.extraWeftYarn?.yarnDesc
//                                    exprod.extraWeftYarnTypeId = brandProduct?.extraWeftYarn?.yarnType?.id
                                        exprod.extraWeftDyeId = brandProduct?.extraWeftDyeId
//                                    exprod.extraWeftDyeDesc = brandProduct?.extraWeftDye?.dyeDesc
                                        exprod.extraWeftYarnCount = brandProduct?.extraWeftYarnCount


                                        exprod.productLength = brandProduct?.length
                                        exprod.productWidth = brandProduct?.width

                                        exprod.reedCountId = brandProduct?.reedCountId
//                                    exprod.reedCount = brandProduct?.reedCount?.count

                                        exprod.productStatusId = brandProduct?.productStatusId

                                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                                        //TODO : Related Product Types to be implemented later

                                        exprod.gsm = brandProduct?.gsm
                                        exprod.weight = brandProduct?.weight
                                        exprod.productSpecs = brandProduct?.productSpec


                                        exprod.createdOn = brandProduct?.created_on
                                        exprod.modifiedOn = brandProduct?.modified_on
//                                    exprod.madeWithAntaran = brandProduct?.madeWithAnthran
                                        exprod.isDeleted = brandProduct?.isDeleted

                                        realm.copyToRealmOrUpdate(exprod)
                                    }else{
                                        nextID = productObj._id ?: 0
                                        productObj.artisanId = brandProduct?.artitionId
//                                    exprod.artisanName = brandProduct?.artistName
//                                    exprod.clusterId = brandProduct?.clusterId
                                        productObj.clusterName = brandProduct?.clusterName
//                                    exprod.brandName = brandProduct?.brand

                                        productObj.productId = brandProduct?.id
                                        productObj.productCode = brandProduct?.code
                                        productObj.productTag = brandProduct?.tag

                                        productObj.productCategoryId = brandProduct?.productCategoryId
                                        productObj.productCategoryDesc = brandProduct?.productCategoryDesc
//                                    exprod.productCategoryCode = brandProduct?.productCategory?.code

                                        productObj.productTypeId = brandProduct?.productTypeId
                                        productObj.productTypeDesc = brandProduct?.productTypeDesc
//                                    exprod.inProductCategory = brandProduct?.productType?.productCategoryId

                                        productObj.warpYarnId = brandProduct?.warpYarnId
//                                    exprod.warpYarnDesc = brandProduct?.warpYarn?.yarnDesc
//                                    exprod.warpYarnTypeId = brandProduct?.warpYarn?.yarnType?.id
//                                    exprod.warpDyeId = brandProduct?.warpYarn?.id
                                        productObj.warpDyeId = brandProduct?.warpDyeId
//                                    exprod.warpDyeDesc = brandProduct?.warpDye?.dyeDesc
                                        productObj.warpYarnCount = brandProduct?.warpYarnCount

                                        productObj.weftYarnId = brandProduct?.weftYarnId
//                                    exprod.weftYarnDesc = brandProduct?.weftYarn?.yarnDesc
//                                    exprod.weftYarnTypeId = brandProduct?.weftYarn?.yarnType?.id
                                        productObj.weftDyeId = brandProduct?.weftDyeId
//                                    exprod.weftDyeDesc = brandProduct?.weftDye?.dyeDesc
                                        productObj.weftYarnCount = brandProduct?.weftYarnCount


                                        productObj.extraWeftYarnId = brandProduct?.extraWeftYarnId
//                                    exprod.extraWeftYarnDesc = brandProduct?.extraWeftYarn?.yarnDesc
//                                    exprod.extraWeftYarnTypeId = brandProduct?.extraWeftYarn?.yarnType?.id
                                        productObj.extraWeftDyeId = brandProduct?.extraWeftDyeId
//                                    exprod.extraWeftDyeDesc = brandProduct?.extraWeftDye?.dyeDesc
                                        productObj.extraWeftYarnCount = brandProduct?.extraWeftYarnCount


                                        productObj.productLength = brandProduct?.length
                                        productObj.productWidth = brandProduct?.width

                                        productObj.reedCountId = brandProduct?.reedCountId
//                                    exprod.reedCount = brandProduct?.reedCount?.count

                                        productObj.productStatusId = brandProduct?.productStatusId

                                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                                        //TODO : Related Product Types to be implemented later

                                        productObj.gsm = brandProduct?.gsm
                                        productObj.weight = brandProduct?.weight
                                        productObj.productSpecs = brandProduct?.productSpec


                                        productObj.createdOn = brandProduct?.created_on
                                        productObj.modifiedOn = brandProduct?.modified_on
//                                    exprod.madeWithAntaran = brandProduct?.madeWithAnthran
                                        productObj.isDeleted = brandProduct?.isDeleted

                                        realm.copyToRealmOrUpdate(productObj)
                                    }
                                }
                            }
                        }
                    }
                }
            }catch (e:Exception){
                Log.e("ArtisanProdLog","$e")
            }
        }

        //TODO : GET

        fun getAllBrandProducts(): RealmResults<BrandList>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(BrandList::class.java).findAll()
        }

        fun getAllCategoryProducts(): RealmResults<CategoryProducts>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(CategoryProducts::class.java).distinct("product").findAll()
        }

        fun getAllClusterProducts(): RealmResults<ClusterProducts>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ClusterProducts::class.java).distinct("clusterName").findAll()
        }

        fun getAllClusters(): RealmResults<ClusterList>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ClusterList::class.java).findAll()
        }

        fun getProductsFromType(type : String?): RealmResults<CategoryProducts>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(CategoryProducts::class.java).equalTo("product",type).findAll()
        }

        fun getBrandProductsFromId(artisanId : Long?): RealmResults<BrandProducts>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(BrandProducts::class.java).equalTo("artisanId",artisanId).findAll()
        }

        fun getFilteredBrandProductsFromId(artisanId : Long?,filter : String?) : RealmResults<BrandProducts>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(BrandProducts::class.java)
                .equalTo("productCategoryName",filter)
                .and()
                .equalTo("artisanId",artisanId)
                .findAll()
        }

        fun getClusterProductsFromId(clusterId : Long?): RealmResults<ClusterProducts>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ClusterProducts::class.java).equalTo("clusterId",clusterId).findAll()
        }

        fun getProductCategoriesOfArtisan(artisanId : Long?) : RealmResults<ArtisanProducts>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ArtisanProducts::class.java)
                .equalTo("artisanId",artisanId)
                .distinct("productCategoryDesc")
                .findAll()
        }

        fun getArtisanProductsByCategory(artisanId : Long?,categoryId : Long?) : RealmResults<ArtisanProducts>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ArtisanProducts::class.java)
                .equalTo("artisanId",artisanId)
                .and()
                .equalTo("productCategoryId",categoryId)
                .findAll()
        }

//        fun filterBrandProducts(artisanId: Long?,filter : Long?) : RealmResults<BrandProducts>?{
//            var realm = Realm.getDefaultInstance()
////            return realm.where(BrandProducts::class.java).equalTo("")
//        }

    }
}