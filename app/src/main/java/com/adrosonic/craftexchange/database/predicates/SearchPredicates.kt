package com.adrosonic.craftexchange.database.predicates

import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import io.realm.Case
import io.realm.RealmResults

class SearchPredicates {
    companion object {
        private var nextID: Long? = 0

        fun buyerSearch(searchFilter : String) : RealmResults<ProductCatalogue>? {
            var realm = CXRealmManager?.getRealmInstance()
            var products : RealmResults<ProductCatalogue> ?= null
            realm?.executeTransaction {
                products = realm.where(ProductCatalogue::class.java)
                    .contains("productTag",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("productCategoryName",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("productTypeDesc",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("product_spe",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("clusterName",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("brandName",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("artisanName",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("artisanName",searchFilter,Case.INSENSITIVE)
                    .findAll()

                products?.size
            }

            return products
        }

        fun artisanSearch(searchFilter : String) : RealmResults<ArtisanProducts>? {
            var realm = CXRealmManager?.getRealmInstance()
            var products : RealmResults<ArtisanProducts> ?= null
            realm?.executeTransaction {
                products = realm.where(ArtisanProducts::class.java)
                    .contains("productCode",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("productCategoryDesc",searchFilter,Case.INSENSITIVE)
                    .or()
                    .contains("productTypeDesc",searchFilter,Case.INSENSITIVE)
//                    .or()
//                    .contains("product_spe",searchFilter,Case.INSENSITIVE) TODO : Search Weave type
                    .findAll()
            }

            return products
        }

    }
}