package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.database.entities.realmEntities.BrandList
import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import io.realm.RealmResults
import java.lang.Exception
import kotlin.math.exp

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
                                        exprod.productCategoryid = product.id
                                        exprod.code = product.code
                                        exprod.product = product.productDesc
                                        exprod.subProductid = catProd.id
                                        exprod.subProduct = catProd.productDesc
                                        exprod.inProductCategory = catProd.productCategoryId


                                        realm.copyToRealmOrUpdate(exprod)
                                    }else{
                                        nextID = productObj._id ?: 0

                                        productObj.productCategoryid = product.id
                                        productObj.code = product.code
                                        productObj.product = product.productDesc
                                        productObj.subProductid = catProd.id
                                        productObj.subProduct = catProd.productDesc
                                        productObj.inProductCategory = catProd.productCategoryId

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

        fun insertProductsInCatalogue(productList: CatalogueProductsResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var addRelatedProducts = arrayListOf<Pair<Long,Long>>()
            var product = productList?.data?.products

            try {
                realm.executeTransaction {
                    var prodItr = product?.iterator()
                    if (prodItr != null) {
                        while (prodItr.hasNext()){
                            var catalogueProduct = prodItr.next()
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
                                var exprod = it.createObject(ProductCatalogue::class.java, nextID)
                                exprod.artisanId = catalogueProduct?.artitionId
                                exprod.artisanName = catalogueProduct?.artistName
                                exprod.clusterId = catalogueProduct?.clusterId
                                exprod.clusterName = catalogueProduct?.clusterName
                                exprod.brandName = catalogueProduct?.brand

                                exprod.productId = catalogueProduct?.id
                                exprod.productCode = catalogueProduct?.code
                                exprod.productTag = catalogueProduct?.tag

                                exprod.productCategoryId = catalogueProduct?.productCategory?.id
                                exprod.productCategoryName = catalogueProduct?.productCategory?.productDesc
                                exprod.productCategoryCode = catalogueProduct?.productCategory?.code

                                exprod.productTypeId = catalogueProduct?.productType?.id
                                exprod.productTypeDesc = catalogueProduct?.productType?.productDesc
                                exprod.inProductCategory = catalogueProduct?.productType?.productCategoryId

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
                            }else{
                                nextID = productObj._id ?: 0
                                productObj.artisanId = catalogueProduct?.artitionId
                                productObj.artisanName = catalogueProduct?.artistName
                                productObj.clusterId = catalogueProduct?.clusterId
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
                                productObj.inProductCategory = catalogueProduct?.productType?.productCategoryId

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
                                        var eximg = it.createObject(ProductImages::class.java, nextID)
                                        eximg.productId = image?.productId
                                        eximg.imageId = image?.id
                                        eximg.imageName = image?.lable

                                        realm.copyToRealmOrUpdate(eximg)
                                    }else{
                                        nextID = imageObj?._id ?: 0
                                        imageObj?.productId = image?.productId
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
                                        var exRelProd= it.createObject(RelatedProducts::class.java, nextID)
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
                                        .equalTo("productId",weaveType.productId)
                                        .and()
                                        .equalTo("weaveId",weaveType.weaveId)
                                        .limit(1)
                                        .findFirst()
                                    if(weaveTypeObj == null){
                                        var primId = it.where(WeaveTypes::class.java).max("_id")
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var exWeaveType = it.createObject(WeaveTypes::class.java, nextID)
                                        exWeaveType?.productId = weaveType?.productId
                                        exWeaveType?.productWeaveId = weaveType?.id
                                        exWeaveType?.weaveId = weaveType?.weaveId
                                        realm.copyToRealmOrUpdate(exWeaveType)
                                    }else{
                                        nextID = weaveTypeObj?._id ?: 0
                                        weaveTypeObj?.productId = weaveType?.productId
                                        weaveTypeObj?.productWeaveId = weaveType?.id
                                        weaveTypeObj?.weaveId = weaveType?.weaveId
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
                                        .equalTo("productId",care.productId)
                                        .and()
                                        .equalTo("productCareId",care.productCareId)
                                        .limit(1)
                                        .findFirst()
                                    if(careObj == null){
                                        var primId = it.where(ProductCares::class.java).max("_id")
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var exCare = it.createObject(ProductCares::class.java, nextID)
                                        exCare?.productId = care?.productId
                                        exCare?.careId = care?.id
                                        exCare?.productCareId = care?.productCareId
                                        realm.copyToRealmOrUpdate(exCare)
                                    }else{
                                        nextID = careObj?._id ?: 0
                                        careObj?.productId = care?.productId
                                        careObj?.careId = care?.id
                                        careObj?.productCareId = care?.productCareId
                                        realm.copyToRealmOrUpdate(careObj)
                                    }

                                }
                            }
                        }
                    }
                }
            }catch (e:Exception){
                Log.e("ProductCatalogueLog","$e")
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

                                    var imageList = brandProduct?.productImages
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
                                                var eximg = it.createObject(ProductImages::class.java, nextID)
                                                eximg.productId = image?.productId
                                                eximg.imageId = image?.id
                                                eximg.imageName = image?.lable

                                                realm.copyToRealmOrUpdate(eximg)
                                            }else{
                                                nextID = imageObj?._id ?: 0
                                                imageObj?.productId = image?.productId
                                                imageObj?.imageId = image?.id
                                                imageObj?.imageName = image?.lable

                                                realm.copyToRealmOrUpdate(imageObj)
                                            }

                                        }
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

        fun getAllBrandDetails(): RealmResults<BrandList>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(BrandList::class.java).findAll()
        }

        fun getBrandDetailsFromId(artisanId : Long?): BrandList? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(BrandList::class.java).equalTo("artisanId",artisanId).limit(1).findFirst()
        }

        fun getAllCategoryProducts(): RealmResults<CategoryProducts>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(CategoryProducts::class.java).distinct("product").findAll()
        }

        fun getAllClusters(): RealmResults<ClusterList>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ClusterList::class.java).findAll()
        }

        fun getBrandProductsFromId(artisanId : Long?): RealmResults<ProductCatalogue>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCatalogue::class.java).equalTo("artisanId",artisanId).findAll()
        }

        fun getFilteredBrands(clusterId : Long?): RealmResults<BrandList>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(BrandList::class.java)
                .equalTo("clusterId",clusterId)
                .findAll()
        }

        fun getFilteredBrandProducts(artisanId : Long?,category : String?): RealmResults<ProductCatalogue>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCatalogue::class.java)
                .equalTo("artisanId",artisanId)
                .and()
                .equalTo("productCategoryName",category)
                .findAll()
        }

        fun getClusterProductsFromId(clusterId : Long?): RealmResults<ProductCatalogue>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCatalogue::class.java).equalTo("clusterId",clusterId).findAll()
        }

        fun getFilteredClusterProducts(clusterId : Long?,category : String?): RealmResults<ProductCatalogue>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCatalogue::class.java)
                .equalTo("clusterId",clusterId)
                .and()
                .equalTo("productCategoryName",category)
                .findAll()
        }

        fun getCategoryProductsFromId(categoryId : Long?): RealmResults<ProductCatalogue>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCatalogue::class.java).equalTo("productCategoryId",categoryId).findAll()
        }

        fun getFilteredCategoryProducts(categoryId : Long?,category : String?): RealmResults<ProductCatalogue>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCatalogue::class.java)
                .equalTo("productCategoryId",categoryId)
                .and()
                .equalTo("clusterName",category)
                .findAll()
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

        fun getFilteredUploadedProducts(artisanId : Long?,category : String?) : RealmResults<ArtisanProducts>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ArtisanProducts::class.java)
                .equalTo("artisanId",artisanId)
                .and()
                .equalTo("productCategoryDesc",category)
                .findAll()
        }

        fun getProductDisplayImage(productId:Long?): ProductImages? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductImages::class.java)
                .equalTo("productId",productId)
                .limit(1)
                .findFirst()
        }

        fun getProductDetails(productId:Long?): ProductCatalogue? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCatalogue::class.java)
                .equalTo("productId", productId)
                .limit(1)
                .findFirst()
        }

        fun getAllImagesOfProduct(productId:Long?): RealmResults<ProductImages>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductImages::class.java)
                .equalTo("productId",productId)
                .findAll()
        }

        fun getWeaveTypesOfProduct(productId: Long?): RealmResults<WeaveTypes>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(WeaveTypes::class.java)
                .equalTo("productId",productId)
                .findAll()
        }

        fun getWashCareInstrctionsOfProduct(productId: Long?): RealmResults<ProductCares>?{
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ProductCares::class.java)
                .equalTo("productId",productId)
                .findAll()
        }

        fun getRelatedProductOfProduct(productId: Long?): RelatedProducts? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(RelatedProducts::class.java)
                .equalTo("relatedToProductId",productId)
                .limit(1)
                .findFirst()
        }

        fun getAllProductIdsOfCategoryFromCluster(category : String?, cluster : String?): MutableList<Long> {
            val realm = CXRealmManager.getRealmInstance()
            var productList = mutableListOf<Long>()
            var list = realm.where(ProductCatalogue::class.java)
                .equalTo("productCategoryName",category)
                .and()
                .equalTo("clusterName",cluster)
                .findAll()

            var itr = list.iterator()
            if(itr !=null){
                while (itr.hasNext()){
                    var product = itr.next()
                    product.productId?.let { productList.add(it) }
                }
            }
            return productList
        }

    }
}