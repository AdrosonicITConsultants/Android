package com.adrosonic.craftexchange.database.predicates

import android.util.Log
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

        fun artisanSearch(searchFilter : String,isMadeWithAntaran : Long) : RealmResults<ArtisanProducts>? {
            var realm = CXRealmManager?.getRealmInstance()
            var products : RealmResults<ArtisanProducts> ?= null
            products?.clear()
            try {
                realm?.executeTransaction {
                    if(isMadeWithAntaran != -1L) {
                        // Antaran/Artisan products
                        products = realm.where(ArtisanProducts::class.java)
                            .equalTo("madeWithAntaran",isMadeWithAntaran).findAll()
                            .where()
                            .contains("productCode", searchFilter, Case.INSENSITIVE)
                            .or()
                            .contains("productCategoryDesc", searchFilter, Case.INSENSITIVE)
                            .or()
                            .contains("productTypeDesc", searchFilter, Case.INSENSITIVE)
                            .or()
                            .contains("productTag",searchFilter, Case.INSENSITIVE)


    //                    .or()
    //                    .contains("product_spe",searchFilter,Case.INSENSITIVE) TODO : Search Weave type
                            .findAll()
                    }else{
                        //All products
                        products = realm.where(ArtisanProducts::class.java)
                            .contains("productCode", searchFilter, Case.INSENSITIVE)
                            .or()
                            .contains("productCategoryDesc", searchFilter, Case.INSENSITIVE)
                            .or()
                            .contains("productTypeDesc", searchFilter, Case.INSENSITIVE)
                            .or()
                            .contains("productTag",searchFilter, Case.INSENSITIVE)

    //                    .or()
    //                    .contains("product_spe",searchFilter,Case.INSENSITIVE) TODO : Search Weave type
                            .findAll()
                    }
                }
            } catch (e: Exception) {
                Log.e("search",e.printStackTrace().toString())
            }
            var size = products?.size
            return products
        }

    }
}