package com.adrosonic.craftexchange.repository.data.request.buyer

open class OwnDesignRequest {
    var productCategoryId: Long=0
    var productTypeId: Long=0
    var productSpec: String=""
    var weight: String=""
    var weaveIds: String? = null
    var gsm: String=""
    var warpDyeId: Long=0
    var warpYarnCount: String=""
    var warpYarnId: Long=0
    var weftDyeId: Long=0
    var weftYarnCount: String=""
    var weftYarnId: Long=0
    var extraWeftDyeId: Long=0
    var extraWeftYarnCount: String=""
    var extraWeftYarnId: Long=0
    var width: String=""
    var length: String=""
    var reedCountId: String=""
    var relatedProduct: String? = null

    override fun toString(): String {

        var string="{" + "\"productCategoryId\":" + productCategoryId +
                ",\"productTypeId\":" + productTypeId +
                ",\"productSpec\":\"" + productSpec + '\"'.toString()

        if(weight.length>0)string=string+",\"weight\":\"" + weight + '\"'.toString()
        if(weaveIds.isNullOrEmpty())string=string+ ",\"weaveIds\":" + weaveIds
        if(!gsm.isNullOrEmpty())string=string+  ",\"gsm\":\"" + gsm + '\"'.toString()
        if(warpDyeId>0)string=string+  ",\"warpDyeId\":" + warpDyeId
        if(!warpYarnCount.isNullOrEmpty())string=string+  ",\"warpYarnCount\":\"" + warpYarnCount + '\"'.toString()
        if(warpYarnId>0)string=string+  ",\"warpYarnId\":" + warpYarnId
        if(weftDyeId>0)string=string+  ",\"weftDyeId\":" + weftDyeId
        if(!weftYarnCount.isNullOrEmpty())string=string+  ",\"weftYarnCount\":\"" + weftYarnCount + '\"'.toString()
        if(weftYarnId>0)string=string+   ",\"weftYarnId\":" + weftYarnId
        if(extraWeftDyeId>0)string=string+  ",\"extraWeftDyeId\":" + extraWeftDyeId
        if(!extraWeftYarnCount.isNullOrEmpty())string=string+  ",\"extraWeftYarnCount\":\"" + extraWeftYarnCount + '\"'.toString()
        if(extraWeftYarnId>0)string=string+ ",\"extraWeftYarnId\":" + extraWeftYarnId
        if(!width.isNullOrEmpty())string=string+ ",\"width\":\"" + width + '\"'.toString()
        if(!length.isNullOrEmpty())string=string+  ",\"length\":\"" + length + '\"'.toString()
        if(!reedCountId.isNullOrEmpty())string=string+ ",\"reedCountId\":\"" + reedCountId + '\"'.toString()
        if(!relatedProduct.isNullOrEmpty())string=string+  ",\"relatedProduct\":" + relatedProduct
        string=string+"}"

        return string
//        "{" + "\"productCategoryId\":" + productCategoryId +
//                ",\"productTypeId\":" + productTypeId +
//                ",\"productSpec\":\"" + productSpec + '\"'.toString() +
//                ",\"weight\":\"" + weight + '\"'.toString() +
//                ",\"weaveIds\":" + weaveIds +
//                ",\"gsm\":\"" + gsm + '\"'.toString() +
//                ",\"warpDyeId\":" + warpDyeId +
//                ",\"warpYarnCount\":\"" + warpYarnCount + '\"'.toString() +
//                ",\"warpYarnId\":" + warpYarnId +
//                ",\"weftDyeId\":" + weftDyeId +
//                ",\"weftYarnCount\":\"" + weftYarnCount + '\"'.toString() +
//                ",\"weftYarnId\":" + weftYarnId +
//                ",\"extraWeftDyeId\":" + extraWeftDyeId +
//                ",\"extraWeftYarnCount\":\"" + extraWeftYarnCount + '\"'.toString() +
//                ",\"extraWeftYarnId\":" + extraWeftYarnId +
//                ",\"width\":\"" + width + '\"'.toString() +
//                ",\"length\":\"" + length + '\"'.toString() +
//                ",\"reedCountId\":\"" + reedCountId + '\"'.toString() +
//                ",\"relatedProduct\":" + relatedProduct + "}"
    }


}
open class RelatedProduct {
    var productTypeID: Long = 0
    var width: String = ""
    var length: String = ""


    override fun toString(): String {
        return "[{" + "\"productTypeId\":" + productTypeID +
                ",\"width\":\"" + width + '\"'.toString() +
                ",\"length\":\"" + length + '\"'.toString() + "}]"
    }
}
