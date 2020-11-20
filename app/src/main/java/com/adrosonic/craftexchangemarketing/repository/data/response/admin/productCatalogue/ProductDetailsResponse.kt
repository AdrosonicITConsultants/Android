package com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue



data class ProductDetailsResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val buyerCustomProduct: Any? = null,
    val product: Product,
    val logo: Any? = null,
    val brand: String,
    val cluster: String
)

data class Product (
    val id: Long,
    val code: String? = null,
    val tag: String? = null,
    val productCategory: ProductCategory? = null,
    val productType: ProductType? = null,
    val warpYarn: Yarn? = null,
    val weftYarn: Yarn? = null,
    val extraWeftYarn: String? = null,
    val warpYarnCount: String? = null,
    val weftYarnCount: String? = null,
    val extraWeftYarnCount: String? = null,
    val warpDye: Dye? = null,
    val weftDye: Dye? = null,
    val extraWeftDye: Dye? = null,
    val length: String? = null,
    val width: String? = null,
    val reedCount: ReedCount? = null,
    val productStatusId: Long? = null,
    val gsm: String? = null,
    val weight: String? = null,
    val product_spe: String? = null,
    val productCares: List<ProductCare>,
    val productWeaves: List<ProductWeaf>,
    val createdOn: String? = null,
    val modifiedOn: String? = null,
    val relProduct: List<Product>,
    val productImages: List<ProductImage>,
    val artitionId: Long? = null,
    val clusterId: Long? = null,
    val marketingUserId: Long? = null,
    val productCategoryDesc: String? = null,
    val productTypeDesc: String? = null,
    val clusterName: String? = null,
    val artistName: String? = null,
    val brand: String? = null,
    val madeWithAnthran: Long,
    val isDeleted: Long? = null,
    val deletedByDeactivation: Long? = null,
    val hibernateLazyInitializer: HibernateLazyInitializer? = null
)

data class Dye (
    val id: Long,
    val dyeDesc: String
)

class HibernateLazyInitializer()

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
    val productCategoryId: Long,
    val productLengths: List<ProductLength>,
    val productWidths: List<ProductWidth>,
    val relatedProductType: List<ProductType?>
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
