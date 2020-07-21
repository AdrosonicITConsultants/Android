package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.entities.UserProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.brandProducts.BrandList
import com.adrosonic.craftexchange.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductDimens
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.brand.BrandProductDetailResponse
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception

class ProductPredicates {
    companion object {
        private var nextID: Long? = 0

        //TODO : INSERT

        fun insertArtisanProductCategory(user: ProfileResponse?) : Long? {
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

        fun insertAllCategoryProducts(prod : AllProductsResponse?){
            nextID = 0L
            val realm = Realm.getDefaultInstance()
            var catProd = prod?.data
            try {
                realm.executeTransaction {
                    var catItr = catProd?.iterator()
                    if (catItr != null) {
                        while (catItr.hasNext()){
                            var product = catItr.next()

                            var catProdType = product.productTypes
                            var prdtypeItr = catProdType?.iterator()
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
                                        exdimen.prodTypeId = catProd.id
                                        exdimen.productType = catProd.productDesc
                                        exdimen.lengthId = pLength.id
                                        exdimen.length = pLength.length

                                        realm.copyToRealmOrUpdate(exdimen)
                                    }else{
                                        nextID = lengthObj._id ?: 0
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

//
//                            if (brandObj == null) {
//                                var primId = it.where(BrandList::class.java).max("_id")
//                                if (primId == null) {
//                                    nextID = 1
//                                } else {
//                                    nextID = primId.toLong() + 1
//                                }
//                                var exbrand = it.createObject(BrandList::class.java, nextID)
//                                exbrand.artisanId = brand.artisanId
//                                exbrand.clusterId = brand.clusterId
//                                exbrand.firstName = brand.firstName
//                                exbrand.companyName = brand.companyName
//                                exbrand.profilePic = brand.profilePic
//                                exbrand.logo = brand.logo
////                                exbrand.productImages = brand.productImages //TODO : to be implemented later
//                                realm.copyToRealmOrUpdate(exbrand)
//                            } else {
//                                nextID = brandObj._id ?: 0
//                                brandObj.artisanId = brand.artisanId
//                                brandObj.clusterId = brand.clusterId
//                                brandObj.firstName = brand.firstName
//                                brandObj.companyName = brand.companyName
//                                brandObj.profilePic = brand.profilePic
//                                brandObj.logo = brand.logo
////                                brandObj.productImages = brand.productImages  //TODO : to be implemented later
//                                realm.copyToRealmOrUpdate(brandObj)
//                            }
//
                        }
//
                    }
                }
            }catch (e:Exception){
                Log.e("prodLog","$e")
            }
        }

        fun insertBrands(list : BrandListResponse?){
            nextID = 0L
            val realm = Realm.getDefaultInstance()
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

        fun insertBrandProducts(productList : BrandProductDetailResponse?){}


        //TODO : GET

        fun getAllBrandProducts(): RealmResults<BrandList>? {
            val realm = Realm.getDefaultInstance()
            return realm.where(BrandList::class.java).findAll()
        }

        fun getAllCategoryProducts(): RealmResults<CategoryProducts>? {
            val realm = Realm.getDefaultInstance()
            return realm.where(CategoryProducts::class.java).distinct("product").findAll()
        }

        fun getProductsFromType(type : String?): RealmResults<CategoryProducts>? {
            val realm = Realm.getDefaultInstance()
            return realm.where(CategoryProducts::class.java).equalTo("product",type).findAll()
        }

    }
}