package com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate



open class ArtisanAddProductRequest{
    var tag: String = ""
     var code: String=""
    var productCategoryId: Long = 0
    var productTypeId: Long = 0
    var productSpec: String = ""
    var weight: String = ""
    var careIds: List<Long>? = null
    var weaveIds: List<Long>? = null
    var statusId: Long = 0
    var gsm: String = ""
    var warpDyeId: Long = 0
    var warpYarnCount: String = ""
    var warpYarnId: Long = 0
    var weftDyeId: Long = 0
    var weftYarnCount: String = ""
    var weftYarnId: Long = 0
    var extraWeftDyeId: Long = 0
    var extraWeftYarnCount: String = ""
    var extraWeftYarnId: Long = 0
    var width: String = ""
    var length: String = ""
    var reedCountId: String = ""
    var relatedProduct: String? = null

    override fun toString(): String {
        return "{"+"\"tag\":\"" + tag + '\"'.toString() +
                ",\"code\":\"" + code + '\"'.toString() +
                ",\"productCategoryId\":"+ productCategoryId +
                ",\"productTypeId\":" + productTypeId +
                ",\"productSpec\":\"" + productSpec +'\"'.toString() +
                ",\"weight\":\"" + weight +'\"'.toString() +
                ",\"careIds\":" + careIds +
                ",\"weaveIds\":"+ weaveIds +
                ",\"statusId\":"+ statusId +
                ",\"gsm\":\""+ gsm+'\"'.toString() +
                ",\"warpDyeId\":"+ warpDyeId+
                ",\"warpYarnCount\":\""+ warpYarnCount+'\"'.toString() +
                ",\"warpYarnId\":"+ warpYarnId+
                ",\"weftDyeId\":" + weftDyeId+
                ",\"weftYarnCount\":\""+weftYarnCount+'\"'.toString() +
                ",\"weftYarnId\":"+weftYarnId+
                ",\"extraWeftDyeId\":"+extraWeftDyeId+
                ",\"extraWeftYarnCount\":\""+extraWeftYarnCount+'\"'.toString() +
                ",\"extraWeftYarnId\":"+extraWeftYarnId+
                ",\"width\":\""+width+'\"'.toString() +
                ",\"length\":\""+length+'\"'.toString() +
                ",\"reedCountId\":\""+reedCountId+'\"'.toString()+
                ",\"relatedProduct\":" + relatedProduct +"}"
    }

}

open class RelatedProduct {
    var productTypeID: Long=0
    var width: String=""
    var length: String=""


    override fun toString(): String {
        return "[{" + "\"productTypeId\":" + productTypeID +
                ",\"width\":\""+width+'\"'.toString() +
                ",\"length\":\""+length+'\"'.toString() + "}]"
    }
}
