package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductCatalogue : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0

    //details of brand
    var artisanId : Long ?= 0
    var artisanName : String ?= ""
    var clusterId : Long ?= 0
    var clusterName : String ?= ""
    var brandName : String ?= ""

    //product main
    var productId : Long ?= 0
    var productCode : String ?=""
    var productTag : String ?=""

    //product catgory
    var productCategoryId : Long ?=0
    var productCategoryName : String ?= ""
    var productCategoryCode : String ?= ""

    //subProduct under category
    //not neccessary to use
//    var subProductId : Long ?=0
//    var subProductName : String ?=""

    //productType
    var productTypeId : Long ?= 0
    var productTypeDesc : String ?= ""
    var inProductCategory : Long ?=0

    //warp Yan
    var warpYarnId : Long ?= 0
    var warpYarnDesc : String ? = ""
    var warpYarnCount : String ? = ""
    var warpYarnTypeId : Long ?= 0
    var warpYarnTypeDesc : String ?= ""
    var warpDyeId : Long ?=0
    var warpDyeDesc : String ?=""

    //weft Yan
    var weftYarnId : Long ?= 0
    var weftYarnDesc : String ? = ""
    var weftYarnCount : String ? = ""
    var weftYarnTypeId : Long ?= 0
    var weftYarnTypeDesc : String ?= ""
    var weftDyeId : Long ?=0
    var weftDyeDesc : String ?=""

    //extra weft Yan
    var extraWeftYarnId : Long ?= 0
    var extraWeftYarnDesc : String ? = ""
    var extraWeftYarnCount : String ? = ""
    var extraWeftYarnTypeId : Long ?= 0
    var extraWeftYarnTypeDesc : String ?= ""
    var extraWeftDyeId : Long ?=0
    var extraWeftDyeDesc : String ?=""

    //product dimensions
    var productLength : String ?= ""
    var productWidth : String ?= ""

    //reed count
    var reedCountId : Long ?= 0
    var reedCount : String ?= ""

    //product status id ... 1-> MadeToOrder.... 2-> Available
    var productStatusId : Long ?=0

    //product specs
    var gsm : String ?=""
    var weight : String ?= ""
    var product_spe : String ?=""

    //TODO : ProductCares, ProductImages and ProductWeaves...different table
    //TODO : Related Product Types to be implemented later

    var createdOn : String ?=""
    var modifiedOn : String ?=""
    var madeWithAntaran : Long ?= 0
    var isDeleted : Long ?= 0
}