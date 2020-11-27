package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.*
import com.adrosonic.craftexchangemarketing.repository.data.editProfile.Cluster
import com.adrosonic.craftexchangemarketing.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.request.editProfileModel.EditArtisanDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.GetAllNotification
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductCatalogueRes
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.login.BuyerResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Data2
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Datum1
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Moq
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.lang.Exception

class ProductCataloguePredicates {
    companion object {
        private val TAG="ArtisanProduct"
        private var nextID : Long? = 0
        var deletedList=ArrayList<Long>()
        fun insertProductCatalogue(data: List<ProductCatalogueRes>, isArtisan:Long) {
            nextID = 0L
           deletedList.clear()
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var prodIterator = data?.iterator()
                    if (prodIterator != null) {
                        while (prodIterator.hasNext()) {
                            var prod = prodIterator.next()
                            var prodObj = realm.where(AdminProductCatalogue::class.java)
                                .equalTo(AdminProductCatalogue.COLUMN_ID, prod.id)
                                .limit(1)
                                .findFirst()
                            val prodId = prodObj?.id ?: 0
                            Log.e(TAG, "222222222222222 : $prodId")
                            deletedList.add(prod.id)
                            if (prodId.equals(prod.id)) {
                                Log.e(TAG, "333333 Update: ${prod.id}")
                                prodObj?.availability = prod.availability
                                prodObj?.id = prod.id
                                prodObj?.brand = prod.brand
                                prodObj?.category = prod.category
                                prodObj?.images = prod.images
                                prodObj?.code = prod.code
                                prodObj?.name = prod.name
                                prodObj?.productID = prod.productId
                                prodObj?.icon = prod.icon
                                prodObj?.dateAdded = prod.dateAdded
                                prodObj?.noOfOrdersGenerated = prod.noOfOrdersGenerated
                                prodObj?.orderGenerated = prod.orderGenerated
                                prodObj?.count = prod.count
                                prodObj?.isArtisan =isArtisan
                                prodObj?.clusterName =prod.clusterName

                                realm.copyToRealmOrUpdate(prodObj)
                            } else {
                                Log.e(TAG, "4444444 Insert")
                                var primId = it.where(AdminProductCatalogue::class.java).max(AdminProductCatalogue.COLUMN__ID)
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var prodObj = it.createObject(AdminProductCatalogue::class.java, nextID)
                                prodObj?.availability = prod.availability
                                prodObj?.id = prod.id
                                prodObj?.brand = prod.brand
                                prodObj?.category = prod.category
                                prodObj?.images = prod.images
                                prodObj?.code = prod.code
                                prodObj?.name = prod.name
                                prodObj?.productID = prod.productId
                                prodObj?.icon = prod.icon
                                prodObj?.dateAdded = prod.dateAdded
                                prodObj?.noOfOrdersGenerated = prod.noOfOrdersGenerated
                                prodObj?.orderGenerated = prod.orderGenerated
                                prodObj?.count = prod.count
                                prodObj?.isArtisan =isArtisan
                                prodObj?.clusterName =prod.clusterName
                                realm.copyToRealmOrUpdate(prodObj)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Notifications", "Insert Exception: ${e}")
            } finally {
//                realm.close()
            }
            if(deletedList.size>0){
                deleteProdCatElement(deletedList)
            }
        }
        fun deleteProdCatElement(deletedList:ArrayList<Long>){

            var realm = CXRealmManager.getRealmInstance()
            var allProducts: RealmResults<AdminProductCatalogue>? =null
            realm.executeTransaction {
                allProducts = realm.where(AdminProductCatalogue::class.java).findAll()
                allProducts?.forEach {
                    if(!deletedList.contains(it.id)) it.deleteFromRealm()
                }
            }
        }
//        fun getAllProducts(isArtisan: Long?): RealmResults<AdminProductCatalogue>? {
//            var realm = CXRealmManager.getRealmInstance()
//            var allProducts: RealmResults<AdminProductCatalogue>? =null
//            realm.executeTransaction {
//                allProducts = realm.where(AdminProductCatalogue::class.java).equalTo(AdminProductCatalogue.COLUMN_IS_ARTISAN, isArtisan).findAll()
//            }
//            return allProducts
//        }

        fun getAllProducts(isArtisan: Long?,search:String,clusterstr: String,availability:String): RealmResults<AdminProductCatalogue>? {

            var realm = CXRealmManager.getRealmInstance()
            var allProducts: RealmResults<AdminProductCatalogue>? =null
            realm.executeTransaction {
                allProducts = if(search.isNullOrEmpty()&& clusterstr.isEmpty() && availability.isEmpty()){
                    realm.where(AdminProductCatalogue::class.java).equalTo(AdminProductCatalogue.COLUMN_IS_ARTISAN, isArtisan)
                        .sort(AdminProductCatalogue.COLUMN_DATE_ADDED, Sort.DESCENDING)
                        .findAll()
                }
                else {

                   val available= if(availability.equals("All"))" " else availability
                   val cluster= if(clusterstr.equals("Select Cluster"))"" else clusterstr
                    Log.e("products","Search search :"+search)
                    Log.e("products","Search cluster :"+cluster)
                    Log.e("products","Search availability :"+available)
                    if(cluster.isEmpty()){
                        realm.where(AdminProductCatalogue::class.java).equalTo(AdminProductCatalogue.COLUMN_IS_ARTISAN, isArtisan)
                            .and()
                            .contains(AdminProductCatalogue.COLUMN_AVAILABILITY,available,Case.INSENSITIVE)
                            .sort(AdminProductCatalogue.COLUMN_DATE_ADDED, Sort.DESCENDING)
                            .findAll().where()
                            .contains(AdminProductCatalogue.COLUMN_CODE,search, Case.INSENSITIVE).or()
                            .contains(AdminProductCatalogue.COLUMN_NAME,search,Case.INSENSITIVE).or()
                            .contains(AdminProductCatalogue.COLUMN_BRAND,search,Case.INSENSITIVE).or()
                            .contains(AdminProductCatalogue.COLUMN_CLUSTER,search,Case.INSENSITIVE).or()
                            .contains(AdminProductCatalogue.COLUMN_CATEGORY,search,Case.INSENSITIVE).or()
                            .findAll()
                    }
                    else realm.where(AdminProductCatalogue::class.java).equalTo(AdminProductCatalogue.COLUMN_IS_ARTISAN, isArtisan)
                        .and()
                        .contains(AdminProductCatalogue.COLUMN_AVAILABILITY,available,Case.INSENSITIVE)
                        .and()
                        .equalTo(AdminProductCatalogue.COLUMN_CLUSTER,cluster,Case.INSENSITIVE)
                        .sort(AdminProductCatalogue.COLUMN_DATE_ADDED, Sort.DESCENDING)
                        .findAll().where()
                        .contains(AdminProductCatalogue.COLUMN_CODE,search, Case.INSENSITIVE).or()
                        .contains(AdminProductCatalogue.COLUMN_NAME,search,Case.INSENSITIVE).or()
                        .contains(AdminProductCatalogue.COLUMN_BRAND,search,Case.INSENSITIVE).or()
                        .contains(AdminProductCatalogue.COLUMN_CLUSTER,search,Case.INSENSITIVE).or()
                        .contains(AdminProductCatalogue.COLUMN_CATEGORY,search,Case.INSENSITIVE).or()
                        .findAll()
                }
            }
            return allProducts
        }

        fun insertProductDetails(details: ProductDetailsResponse?){
            nextID = 0L
            Log.e("EnquiryProduct","details ${details?.data?.brand}")
            val realm = CXRealmManager.getRealmInstance()
            val prod = details?.data?.product
            try {
                realm.executeTransaction {
                    var productObj = realm.where(EnquiryProductDetails::class.java)
                        .equalTo(EnquiryProductDetails.COLUMN_PRODUCT_ID,prod?.id)
                        .limit(1)
                        .findFirst()
                    if (productObj == null){
                        var primId = it.where(EnquiryProductDetails::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var exprod = it.createObject(EnquiryProductDetails::class.java,
                            nextID
                        )
                        Log.e("EnquiryProduct","1111111 ${prod?.artitionId}")
                        exprod.isCustom = false
                        exprod.artisanId = prod?.artitionId
                        exprod.artisanName = prod?.artistName
                        exprod.clusterId = prod?.clusterId
                        exprod.clusterName = prod?.clusterName
                        Log.e("EnquiryProduct","22222222 ${prod?.id}")
//                        exprod.brandName = catalogueProduct?.brand
//                        productId= catalogueProduct?.id
                        exprod.productId = prod?.id
                        exprod.productCode = prod?.code
                        exprod.productTag = prod?.tag

                        exprod.productCategoryId = prod?.productCategory?.id
                        exprod.productCategoryName = prod?.productCategory?.productDesc
                        exprod.productCategoryCode = prod?.productCategory?.code
                        Log.e("EnquiryProduct","333333333333 ${prod?.id}")
                        exprod.productTypeId = prod?.productType?.id
                        exprod.productTypeDesc = prod?.productType?.productDesc
                        exprod.inProductCategory = prod?.productType?.productCategoryId
                        Log.e("EnquiryProduct","444444444444 ${prod?.id}")
                        exprod.warpYarnId = prod?.warpYarn?.id
                        exprod.warpYarnDesc = prod?.warpYarn?.yarnDesc
                        exprod.warpYarnCount = prod?.warpYarnCount

                        Log.e("EnquiryProduct","55555555 ${prod?.id}")
                        exprod.warpDyeId = prod?.warpYarn?.id
                        exprod.warpDyeId = prod?.warpDye?.id
                        exprod.warpDyeDesc = prod?.warpDye?.dyeDesc
                        Log.e("EnquiryProduct","666666666 ${prod?.id}")
                        exprod.weftYarnId = prod?.weftYarn?.id
                        exprod.weftYarnDesc = prod?.weftYarn?.yarnDesc
                        exprod.weftYarnCount = prod?.weftYarnCount
//                                exprod.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.weftDyeId = prod?.weftDye?.id
                        exprod.weftDyeDesc = prod?.weftDye?.dyeDesc
                        Log.e("EnquiryProduct","77777777 ${prod?.id}")
//                        exprod.extraWeftYarnId = prod?.extraWeftYarn?:
                        exprod.extraWeftYarnDesc = prod?.extraWeftYarn?:""
                        exprod.extraWeftYarnCount = prod?.extraWeftYarnCount
//                                exprod.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.extraWeftDyeId = prod?.extraWeftDye?.id
                        exprod.extraWeftDyeDesc = prod?.extraWeftDye?.dyeDesc
                        Log.e("EnquiryProduct","88888888888 ${prod?.id}")
                        exprod.productLength = prod?.length
                        exprod.productWidth = prod?.width

                        exprod.reedCountId = prod?.reedCount?.id
                        exprod.reedCount = prod?.reedCount?.count
                        Log.e("EnquiryProduct","99999999999 ${prod?.id}")
                        exprod.productStatusId = prod?.productStatusId

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        exprod.gsm = prod?.gsm
                        exprod.weight = prod?.weight
                        exprod.product_spe = prod?.product_spe
                        Log.e("EnquiryProduct","aaaaaaaaaaa ${prod?.id}")
//                        exprod.createdOn = catalogueProduct?.createdOn
//                        exprod.modifiedOn = catalogueProduct?.modifiedOn
//                        exprod.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        exprod.isDeleted = prod?.isDeleted
                        Log.e("EnquiryProduct","bbbbbbbbbbb ${prod?.id}")
                        realm.copyToRealmOrUpdate(exprod)
                    }
                    else{
                        nextID = productObj._id ?: 0
                        productObj.isCustom = false
                        productObj.artisanId = prod?.artitionId
                        productObj.artisanName = prod?.artistName
                        productObj.clusterId = prod?.clusterId
                        productObj.clusterName = prod?.clusterName
//                        productObj.brandName = catalogueProduct?.brand

                        productObj.productId = prod?.id
                        productObj.productCode = prod?.code
                        productObj.productTag = prod?.tag

                        productObj.productCategoryId = prod?.productCategory?.id
                        productObj.productCategoryName = prod?.productCategory?.productDesc
                        productObj.productCategoryCode = prod?.productCategory?.code

                        productObj.productTypeId = prod?.productType?.id
                        productObj.productTypeDesc = prod?.productType?.productDesc
                        productObj.inProductCategory = prod?.productType?.productCategoryId

                        productObj.warpYarnId = prod?.warpYarn?.id
                        productObj.warpYarnDesc = prod?.warpYarn?.yarnDesc
                        productObj.warpYarnCount = prod?.warpYarnCount
//                                productObj.warpYarnTypeId = catalogueProduct?.warpYarn?.yarnType?.id TODO : to be used later if required
                        productObj.warpDyeId = prod?.warpYarn?.id
                        productObj.warpDyeId = prod?.warpDye?.id
                        productObj.warpDyeDesc = prod?.warpDye?.dyeDesc

                        productObj.weftYarnId = prod?.weftYarn?.id
                        productObj.weftYarnDesc = prod?.weftYarn?.yarnDesc
                        productObj.weftYarnCount = prod?.weftYarnCount
//                                productObj.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.weftDyeId = prod?.weftDye?.id
                        productObj.weftDyeDesc = prod?.weftDye?.dyeDesc

//                        productObj.extraWeftYarnId = prod?.extraWeftYarn?.id
                        productObj.extraWeftYarnDesc = prod?.extraWeftYarn
                        productObj.extraWeftYarnCount = prod?.extraWeftYarnCount
//                                productObj.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.extraWeftDyeId = prod?.extraWeftDye?.id
                        productObj.extraWeftDyeDesc = prod?.extraWeftDye?.dyeDesc

                        productObj.productLength = prod?.length
                        productObj.productWidth = prod?.width

                        productObj.reedCountId = prod?.reedCount?.id
                        productObj.reedCount = prod?.reedCount?.count

                        productObj.productStatusId = prod?.productStatusId

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        productObj.gsm = prod?.gsm
                        productObj.weight = prod?.weight
                        productObj.product_spe = prod?.product_spe

//                        productObj.createdOn = catalogueProduct?.createdOn
//                        productObj.modifiedOn = catalogueProduct?.modifiedOn
//                        productObj.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        productObj.isDeleted = prod?.isDeleted

                        realm.copyToRealmOrUpdate(productObj)
                    }

                    var imageList = prod?.productImages
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
                                eximg.productId = prod?.id
                                eximg.imageId = image?.id
                                eximg.imageName = image?.lable
                                realm.copyToRealmOrUpdate(eximg)
                            }else{
                                nextID = imageObj._id ?: 0
                                imageObj.productId = prod?.id
                                imageObj.imageId = image?.id
                                imageObj.imageName = image?.lable
                                realm.copyToRealmOrUpdate(imageObj)
                            }

                        }
                    }

                    var relatedProductList = prod?.relProduct
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
                                exRelProd?.inProductCategoryId = prod?.productCategory?.id
                                exRelProd?.relatedToProductId = prod?.id
                                exRelProd?.relatedProductId = relProduct?.id
                                exRelProd?.productTypeId = relProduct?.productType?.id
                                exRelProd?.productName = relProduct?.productType?.productDesc
                                exRelProd?.productLength = relProduct?.length
                                exRelProd?.productWidth = relProduct?.width
                                exRelProd?.productWeight = relProduct?.weight

                                realm.copyToRealmOrUpdate(exRelProd)
                            }else{
                                relPoprductObj.inProductCategoryId = prod?.productCategory?.id
                                relPoprductObj.relatedToProductId = prod?.id
                                relPoprductObj.relatedProductId = relProduct?.id
                                relPoprductObj.productTypeId = relProduct?.productType?.id
                                relPoprductObj.productName = relProduct?.productType?.productDesc
                                relPoprductObj.productLength = relProduct?.length
                                relPoprductObj.productWidth = relProduct?.width
                                relPoprductObj.productWeight = relProduct?.weight

                                realm.copyToRealmOrUpdate(relPoprductObj)
                            }
                        }
                    }

                    var weaveTypeList = prod?.productWeaves
                    var weaveTypeItr = weaveTypeList?.iterator()
                    if(weaveTypeItr!=null){
                        while (weaveTypeItr.hasNext()){
                            var weaveType = weaveTypeItr.next()

                            var weaveTypeObj = realm.where(WeaveTypes::class.java)
                                .equalTo("productId",weaveType.productId)
                                .and()
                                .equalTo("weaveId",weaveType.weaveId)
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
                                exWeaveType?.productId = weaveType?.productId
                                exWeaveType?.productWeaveId = weaveType?.id
                                exWeaveType?.weaveId = weaveType?.weaveId
                                realm.copyToRealmOrUpdate(exWeaveType)
                            }else{
                                nextID = weaveTypeObj._id ?: 0
                                weaveTypeObj.productId = weaveType?.productId
                                weaveTypeObj.productWeaveId = weaveType?.id
                                weaveTypeObj.weaveId = weaveType?.weaveId
                                realm.copyToRealmOrUpdate(weaveTypeObj)
                            }

                        }
                    }

                    var careList = prod?.productCares
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
                                var exCare = it.createObject(
                                    ProductCares::class.java,
                                    nextID
                                )
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

            }catch (e:Exception){
                Log.e("EnquiryProduct","$e")
            }
        }

        fun insertCustomProductDetails(details: ProductDetailsResponse?){
            nextID = 0L
            Log.e("EnquiryProduct","details ${details?.data?.brand}")
            val realm = CXRealmManager.getRealmInstance()
            val prod = details?.data?.buyerCustomProduct
            try {
                realm.executeTransaction {
                    var productObj = realm.where(EnquiryProductDetails::class.java)
                        .equalTo(EnquiryProductDetails.COLUMN_PRODUCT_ID,prod?.id)
                        .limit(1)
                        .findFirst()
                    if (productObj == null){
                        var primId = it.where(EnquiryProductDetails::class.java).max("_id")
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var exprod = it.createObject(EnquiryProductDetails::class.java,
                            nextID
                        )
                        Log.e("ProductDetails","1111111 ${prod?.buyerID}")
                        exprod.isCustom = false
                        exprod.artisanId = prod?.buyerID
                        exprod.artisanName = "NA"
                        exprod.clusterId =0
                        exprod.clusterName = "NA"
                        Log.e("ProductDetails","22222222 ${prod?.id}")
//                        exprod.brandName = catalogueProduct?.brand
//                        productId= catalogueProduct?.id
                        exprod.productId = prod?.id
                        exprod.productCode =  prod?.productCategory?.code
                        exprod.productTag = prod?.productCategory?.productTypes?.get(0)?.productDesc

                        exprod.productCategoryId = prod?.productCategory?.id
                        exprod.productCategoryName = prod?.productCategory?.productDesc
                        exprod.productCategoryCode = prod?.productCategory?.code
                        Log.e("ProductDetails","333333333333 ${prod?.id}")
                        exprod.productTypeId = prod?.productType?.id
                        exprod.productTypeDesc = prod?.productType?.productDesc
                        exprod.inProductCategory = prod?.productType?.productCategoryId
                        Log.e("ProductDetails","444444444444 ${prod?.id}")
                        exprod.warpYarnId = prod?.warpYarn?.id
                        exprod.warpYarnDesc = prod?.warpYarn?.yarnDesc
                        exprod.warpYarnCount = prod?.warpYarnCount

                        Log.e("ProductDetails","55555555 ${prod?.id}")
                        exprod.warpDyeId = prod?.warpYarn?.id
                        exprod.warpDyeId = prod?.warpDye?.id
                        exprod.warpDyeDesc = prod?.warpDye?.dyeDesc
                        Log.e("ProductDetails","666666666 ${prod?.id}")
                        exprod.weftYarnId = prod?.weftYarn?.id
                        exprod.weftYarnDesc = prod?.weftYarn?.yarnDesc
                        exprod.weftYarnCount = prod?.weftYarnCount
//                                exprod.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.weftDyeId = prod?.weftDye?.id
                        exprod.weftDyeDesc = prod?.weftDye?.dyeDesc
                        Log.e("ProductDetails","77777777 ${prod?.id}")
//                        exprod.extraWeftYarnId = prod?.extraWeftYarn?:
                        exprod.extraWeftYarnDesc = prod?.extraWeftYarn?.yarnDesc
                        exprod.extraWeftYarnCount = prod?.extraWeftYarnCount
//                                exprod.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        exprod.extraWeftDyeId = prod?.extraWeftDye?.id
                        exprod.extraWeftDyeDesc = prod?.extraWeftDye?.dyeDesc
                        Log.e("ProductDetails","88888888888 ${prod?.id}")
                        exprod.productLength = prod?.length
                        exprod.productWidth = prod?.width

                        exprod.reedCountId = prod?.reedCount?.id
                        exprod.reedCount = prod?.reedCount?.count
                        Log.e("ProductDetails","99999999999 ${prod?.id}")
                        exprod.productStatusId = 0

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        exprod.gsm = prod?.gsm
                        exprod.weight = prod?.weight
                        exprod.product_spe = prod?.productSpec
                        Log.e("ProductDetails","aaaaaaaaaaa ${prod?.id}")
//                        exprod.createdOn = catalogueProduct?.createdOn
//                        exprod.modifiedOn = catalogueProduct?.modifiedOn
//                        exprod.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        exprod.isDeleted = prod?.isDeleted
                        Log.e("ProductDetails","bbbbbbbbbbb ${prod?.id}")
                        realm.copyToRealmOrUpdate(exprod)
                    }
                    else{
                        nextID = productObj._id ?: 0
                        productObj.isCustom = false
                        productObj.artisanId = prod?.buyerID
                        productObj.artisanName = "NA"
                        productObj.clusterId = 0
                        productObj.clusterName = "NA"
//                        productObj.brandName = catalogueProduct?.brand

                        productObj.productId = prod?.id
                        productObj.productCode = prod?.productCategory?.code
                        productObj.productTag =prod?.productCategory?.productTypes?.get(0)?.productDesc

                        productObj.productCategoryId = prod?.productCategory?.id
                        productObj.productCategoryName = prod?.productCategory?.productDesc
                        productObj.productCategoryCode = prod?.productCategory?.code

                        productObj.productTypeId = prod?.productType?.id
                        productObj.productTypeDesc = prod?.productType?.productDesc
                        productObj.inProductCategory = prod?.productType?.productCategoryId

                        productObj.warpYarnId = prod?.warpYarn?.id
                        productObj.warpYarnDesc = prod?.warpYarn?.yarnDesc
                        productObj.warpYarnCount = prod?.warpYarnCount
//                                productObj.warpYarnTypeId = catalogueProduct?.warpYarn?.yarnType?.id TODO : to be used later if required
                        productObj.warpDyeId = prod?.warpYarn?.id
                        productObj.warpDyeId = prod?.warpDye?.id
                        productObj.warpDyeDesc = prod?.warpDye?.dyeDesc

                        productObj.weftYarnId = prod?.weftYarn?.id
                        productObj.weftYarnDesc = prod?.weftYarn?.yarnDesc
                        productObj.weftYarnCount = prod?.weftYarnCount
//                                productObj.weftYarnTypeId = catalogueProduct?.weftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.weftDyeId = prod?.weftDye?.id
                        productObj.weftDyeDesc = prod?.weftDye?.dyeDesc

//                        productObj.extraWeftYarnId = prod?.extraWeftYarn?.id
                        productObj.extraWeftYarnDesc = prod?.extraWeftYarn?.yarnDesc
                        productObj.extraWeftYarnCount = prod?.extraWeftYarnCount
//                                productObj.extraWeftYarnTypeId = catalogueProduct?.extraWeftYarn?.yarnType?.id TODO : to be used later if required
                        productObj.extraWeftDyeId = prod?.extraWeftDye?.id
                        productObj.extraWeftDyeDesc = prod?.extraWeftDye?.dyeDesc

                        productObj.productLength = prod?.length
                        productObj.productWidth = prod?.width

                        productObj.reedCountId = prod?.reedCount?.id
                        productObj.reedCount = prod?.reedCount?.count

                        productObj.productStatusId = 2

                        //TODO : ProductCares, ProductImages and ProductWeaves...different table
                        //TODO : Related Product Types to be implemented later

                        productObj.gsm = prod?.gsm
                        productObj.weight = prod?.weight
                        productObj.product_spe = prod?.productSpec

//                        productObj.createdOn = catalogueProduct?.createdOn
//                        productObj.modifiedOn = catalogueProduct?.modifiedOn
//                        productObj.madeWithAntaran = catalogueProduct?.madeWithAnthran
                        productObj.isDeleted = prod?.isDeleted

                        realm.copyToRealmOrUpdate(productObj)
                    }

                    var imageList = prod?.productImages
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
                                eximg.productId = prod?.id
                                eximg.imageId = image?.id
                                eximg.imageName = image?.lable
                                realm.copyToRealmOrUpdate(eximg)
                            }else{
                                nextID = imageObj._id ?: 0
                                imageObj.productId = prod?.id
                                imageObj.imageId = image?.id
                                imageObj.imageName = image?.lable
                                realm.copyToRealmOrUpdate(imageObj)
                            }

                        }
                    }

                    var relatedProductList = prod?.relProduct
                    var prodItr = relatedProductList?.iterator()
                    if(prodItr!=null){
                        while (prodItr.hasNext()){
                            var relProduct = prodItr.next()

                            var relPoprductObj = realm.where(RelatedProducts::class.java)
                                .equalTo("relatedProductId",relProduct.productType.id)
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
                                exRelProd?.inProductCategoryId = prod?.productCategory?.id
                                exRelProd?.relatedToProductId = prod?.id
                                exRelProd?.relatedProductId = relProduct?.id
                                exRelProd?.productTypeId = relProduct?.productType?.id
                                exRelProd?.productName = relProduct?.productType?.productDesc
                                exRelProd?.productLength = relProduct?.length
                                exRelProd?.productWidth = relProduct?.width
                                exRelProd?.productWeight = relProduct?.weight
                                realm.copyToRealmOrUpdate(exRelProd)
                            }else{
                                relPoprductObj.inProductCategoryId = prod?.productCategory?.id
                                relPoprductObj.relatedToProductId = prod?.id
                                relPoprductObj.relatedProductId = relProduct?.id
                                relPoprductObj.productTypeId = relProduct?.productType?.id
                                relPoprductObj.productName = relProduct?.productType?.productDesc
                                relPoprductObj.productLength = relProduct?.length
                                relPoprductObj.productWidth = relProduct?.width
                                relPoprductObj.productWeight = relProduct?.weight

                                realm.copyToRealmOrUpdate(relPoprductObj)
                            }
                        }
                    }
                    var weaveTypeList = prod?.productWeaves
                    var weaveTypeItr = weaveTypeList?.iterator()
                    if(weaveTypeItr!=null){
                        while (weaveTypeItr.hasNext()){
                            var weaveType = weaveTypeItr.next()

                            var weaveTypeObj = realm.where(WeaveTypes::class.java)
                                .equalTo("productId",weaveType.productId)
                                .and()
                                .equalTo("weaveId",weaveType.weaveId)
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
                                exWeaveType?.productId = weaveType?.productId
                                exWeaveType?.productWeaveId = weaveType?.id
                                exWeaveType?.weaveId = weaveType?.weaveId
                                realm.copyToRealmOrUpdate(exWeaveType)
                            }else{
                                nextID = weaveTypeObj._id ?: 0
                                weaveTypeObj.productId = weaveType?.productId
                                weaveTypeObj.productWeaveId = weaveType?.id
                                weaveTypeObj.weaveId = weaveType?.weaveId
                                realm.copyToRealmOrUpdate(weaveTypeObj)
                            }

                        }
                    }

//                    var careList = prod?.productCares
//                    var careItr = careList?.iterator()
//                    if(careItr!=null){
//                        while (careItr.hasNext()){
//                            var care = careItr.next()
//
//                            var careObj = realm.where(ProductCares::class.java)
//                                .equalTo("productId",care.productId)
//                                .and()
//                                .equalTo("productCareId",care.productCareId)
//                                .limit(1)
//                                .findFirst()
//                            if(careObj == null){
//                                var primId = it.where(ProductCares::class.java).max("_id")
//                                if (primId == null) {
//                                    nextID = 1
//                                } else {
//                                    nextID = primId.toLong() + 1
//                                }
//                                var exCare = it.createObject(
//                                    ProductCares::class.java,
//                                    nextID
//                                )
//                                exCare?.productId = care?.productId
//                                exCare?.careId = care?.id
//                                exCare?.productCareId = care?.productCareId
//                                realm.copyToRealmOrUpdate(exCare)
//                            }else{
//                                nextID = careObj?._id ?: 0
//                                careObj?.productId = care?.productId
//                                careObj?.careId = care?.id
//                                careObj?.productCareId = care?.productCareId
//                                realm.copyToRealmOrUpdate(careObj)
//                            }
//
//                        }
//                    }
                }

            }catch (e:Exception){
                Log.e("EnquiryProduct","$e")
            }
        }

        fun deleteProductEntry(id:Long){
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                val artisonProd = it.where(EnquiryProductDetails::class.java).equalTo(EnquiryProductDetails.COLUMN_PRODUCT_ID, id).findAll()
                artisonProd.deleteAllFromRealm()

                val cat = it.where(AdminProductCatalogue::class.java).equalTo(AdminProductCatalogue.COLUMN_PRODUCT_ID, id).
                or().equalTo(AdminProductCatalogue.COLUMN_ID,id).findAll()
                cat.deleteAllFromRealm()

            }

        }

    }
}