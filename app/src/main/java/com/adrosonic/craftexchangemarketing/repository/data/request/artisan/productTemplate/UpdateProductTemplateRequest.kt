package com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate

//data class UpdateProductTemplateRequest (
//    val productCares: List<ProductCare>,
//    val productWeaves: List<ProductWeaf>,
//    val warpDyeId: Long,
//    val warpYarnCount: String,
//    val warpYarnId: Long,
//    val weftDyeId: Long,
//    val weftYarnCount: String,
//    val weftYarnId: Long,
//    val extraWeftDyeId: String,
//    val extraWeftYarnCount: String? = null,
//    val extraWeftYarnId: String,
//    val id: String,
//    val tag: String,
//    val code: String,
//    val productCategoryId: Long,
//    val productTypeId: Long,
//    val productSpec: String,
//    val weight: String,
//    val productStatusId: Long,
//    val gsm: String,
//    val width: String,
//    val length: String,
//    val reedCountId: Long,
//    val relProduct: List<RelProduct>
//)
//data class RelProduct(
//    val productTypeId: Long,
//    val width: String,
//    val length: String
//)
//
//data class ProductCare (
//    val id: Long,
//    val productCareId: Long,
//    val productId: String
//)
//
//data class ProductWeaf (
//    val id: Long,
//    val weaveId: Long
//)
open class UpdateProductTemplateRequest{
    var id: String = ""
    var tag: String = ""
    var code: String=""
    var productCategoryId: Long = 0
    var productTypeId: Long = 0
    var productSpec: String = ""
    var weight: String = ""
    var productCares: String? = null
    var productWeaves: String? = null
    var statusId: Long = 0
    var gsm: String = ""
    var warpDyeId: Long = 0
    var warpYarnCount: String = ""
    var warpYarnId: Long = 0
    var weftDyeId: Long = 0
    var weftYarnCount: String = ""
    var weftYarnId: Long = 0
    var extraWeftDyeId: String = ""
    var extraWeftYarnCount: String = ""
    var extraWeftYarnId: String = ""
    var width: String = ""
    var length: String = ""
    var reedCountId: Long = 0
    var relatedProduct: String? = null

    override fun toString(): String {
        return "{"+"\"tag\":\"" + tag + '\"'.toString() +
                ",\"id\":\"" + id + '\"'.toString() +
                ",\"code\":\"" + code + '\"'.toString() +
                ",\"productCategoryId\":"+ productCategoryId +
                ",\"productTypeId\":" + productTypeId +
                ",\"productSpec\":\"" + productSpec +'\"'.toString() +
                ",\"weight\":\"" + weight +'\"'.toString() +
                ",\"productCares\":" + productCares +
                ",\"productWeaves\":"+ productWeaves +
                ",\"productStatusId\":"+ statusId +
                ",\"gsm\":\""+ gsm+'\"'.toString() +
                ",\"warpDyeId\":"+ warpDyeId+
                ",\"warpYarnCount\":\""+ warpYarnCount+'\"'.toString() +
                ",\"warpYarnId\":"+ warpYarnId+
                ",\"weftDyeId\":" + weftDyeId+
                ",\"weftYarnCount\":\""+weftYarnCount+'\"'.toString() +
                ",\"weftYarnId\":"+weftYarnId+
                ",\"extraWeftDyeId\":\""+extraWeftDyeId+'\"'.toString() +
                ",\"extraWeftYarnCount\":\""+extraWeftYarnCount+'\"'.toString() +
                ",\"extraWeftYarnId\":\""+extraWeftYarnId+'\"'.toString() +
                ",\"width\":\""+width+'\"'.toString() +
                ",\"length\":\""+length+'\"'.toString() +
                ",\"reedCountId\":\""+reedCountId+'\"'.toString()+
                ",\"relProduct\":" + relatedProduct +"}"
    }

}

//open class RelatedProduct {
//    var productTypeID: Long=0
//    var width: String=""
//    var length: String=""
//
//
//    override fun toString(): String {
//        return "[{" + "\"productTypeId\":" + productTypeID +
//                ",\"width\":\""+width+'\"'.toString() +
//                ",\"length\":\""+length+'\"'.toString() + "}]"
//    }
//}

open class ProductCare {
    var id: Long=0
    var productCareId: Long=0
    var productId: String=""
    override fun toString(): String {
        return "{" + "\"id\":" + id +
                ",\"productCareId\":" +productCareId+
                ",\"productId\":\""+productId+'\"'.toString() + "}"
    }
}

open class ProductWeaf {
    var id: Long=0
    var weaveId: Long=0


    override fun toString(): String {
        return "{" + "\"id\":" + id +
                ",\"weaveId\":" +weaveId +"}"
    }
}
