package com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.cluster


data class ClusterProductDetailResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val products: List<Product>,
    val cluster: Cluster
)

data class Cluster (
    val id: Long,
    val desc: String,
    val adjective: String,
    val hibernateLazyInitializer: HibernateLazyInitializer
)


class HibernateLazyInitializer()

data class Product (
    val id: Long,
    val code: String? = "",
    val tag: String? = "",
    val productCategory: ProductCategory? = null,
    val productType: ProductType,
    val warpYarn: Yarn? = null,
    val weftYarn: Yarn? = null,
    val extraWeftYarn: Yarn? = null,
    val warpYarnCount: String? = "",
    val weftYarnCount: String? = "",
    val extraWeftYarnCount: String? = "",
    val warpDye: Dye? = null,
    val weftDye: Dye? = null,
    val extraWeftDye: Dye? = null,
    val length: String,
    val width: String,
    val reedCount: ReedCount? = null,
    val productStatusId: Long? = 0,
    val gsm: String? = "",
    val weight: String? = "",
    val product_spe: String? = "",
    val productCares: List<ProductCare>,
    val productWeaves: List<ProductWeaf>,
    val createdOn: String? = "",
    val modifiedOn: String? = "",
    val relProduct: List<Product>,
    val productImages: List<ProductImage>,
    val artitionId: Long? = 0,
    val clusterId: Long? = 0,
    val productCategoryDesc: String? = "",
    val productTypeDesc: String? = "",
    val clusterName: String? = "",
    val artistName: String? = "",
    val brand: String? = "",
    val madeWithAnthran: Long? = 0,
    val isDeleted: Long? = 0
)


data class Dye (
    val id: Long,
    val dyeDesc: String
)


data class Yarn (
    val id: Long,
    val yarnDesc: String,
    val yarnType: YarnType
)

data class YarnType (
    val id: Long,
    val yarnTypeDesc: String,
    val yarnCounts: List<YarnCount>,
    val manual: Boolean
)

data class YarnCount (
    val id: Long,
    val count: String,
    val yarnTypeId: Long
)

data class ProductCare (
    val id: Long,
    val productId: Long,
    val productCareId: Long
)

data class ProductCategory (
    val id: Long,
    val productDesc: String,
    val code: String,
    val productTypes: List<ProductType>
)

data class ProductType (
    val id: Long,
    val productDesc: String,
    val productCategoryId: Long? = 0,
    val productLengths: List<ProductLength>,
    val productWidths: List<ProductWidth>,
    val relatedProductType: List<ProductType>
)

data class ProductLength (
    val id: Long,
    val length: String,
    val productTypeId: Long
)

data class ProductWidth (
    val id: Long,
    val width: String,
    val productTypeId: Long
)

data class ProductImage (
    val id: Long,
    val lable: String,
    val productId: Long
)

data class ProductWeaf (
    val id: Long,
    val productId: Long,
    val weaveId: Long
)

data class ReedCount (
    val id: Long,
    val count: String
)