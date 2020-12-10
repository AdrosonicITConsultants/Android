package com.adrosonic.craftexchange.repository.data.request.buyer

open class UpdateOwnDesignRequest {
    var extraWeftDyeId: Long=0
    var extraWeftYarnCount: String=""
    var extraWeftYarnId: Long=0
    var gsm: String=""
    var id: Long=0
    var length: String=""
    var productCategoryId: Long=0
    var productTypeId: Long=0
    var productWeaves:String=""
    var productSpec: String=""
    var reedCountId: Long=0
    var relProduct=""
    var warpDyeId: Long=0
    var warpYarnCount: String=""
    var warpYarnId: Long=0
    var weftDyeId: Long=0
    var weftYarnCount: String=""
    var weftYarnId: Long=0
    var weight: String=""
    var width: String=""



//data class RelProduct (
//    val productTypeId: Long,
//    val width: String,
//    val length: String
//)

    override fun toString(): String {

        var string = "{" + "\"productCategoryId\":" + productCategoryId +
                ",\"productTypeId\":" + productTypeId +
                ",\"productSpec\":\"" + productSpec + '\"'.toString()+
                ",\"id\":" + id

        if (weight.length > 0) string = string + ",\"weight\":\"" + weight + '\"'.toString()
        if (!productWeaves.isNullOrEmpty()) string = string + ",\"productWeaves\":" + productWeaves
        if (!gsm.isNullOrEmpty()) string = string + ",\"gsm\":\"" + gsm + '\"'.toString()
        if (warpDyeId > 0) string = string + ",\"warpDyeId\":" + warpDyeId
        if (!warpYarnCount.isNullOrEmpty()) string =
            string + ",\"warpYarnCount\":\"" + warpYarnCount + '\"'.toString()
        if (warpYarnId > 0) string = string + ",\"warpYarnId\":" + warpYarnId
        if (weftDyeId > 0) string = string + ",\"weftDyeId\":" + weftDyeId
        if (!weftYarnCount.isNullOrEmpty()) string =
            string + ",\"weftYarnCount\":\"" + weftYarnCount + '\"'.toString()
        if (weftYarnId > 0) string = string + ",\"weftYarnId\":" + weftYarnId
        if (extraWeftDyeId > 0) string = string + ",\"extraWeftDyeId\":" + extraWeftDyeId
        if (!extraWeftYarnCount.isNullOrEmpty()) string =
            string + ",\"extraWeftYarnCount\":\"" + extraWeftYarnCount + '\"'.toString()
        if (extraWeftYarnId > 0) string = string + ",\"extraWeftYarnId\":" + extraWeftYarnId
        if (!width.isNullOrEmpty()) string = string + ",\"width\":\"" + width + '\"'.toString()
        if (!length.isNullOrEmpty()) string = string + ",\"length\":\"" + length + '\"'.toString()
        if (reedCountId>0) string =
            string + ",\"reedCountId\":\"" + reedCountId + '\"'.toString()
        string = string + "}"

        return string
    }
    }

open class ProductWeaf {
    var id: Long = 0
    var productId: Long = 0
    var weaveId: Long = 0


    override fun toString(): String {
        return "{" + "\"id\":" + id +
                ",\"productId\":\"" + productId + '\"'.toString()+
                ",\"weaveId\":" + weaveId  + "}"
    }
}
//data class ProductWeaf (
//    val id: Long,
//    val productId: Long,
//    val weaveId: Long
//)