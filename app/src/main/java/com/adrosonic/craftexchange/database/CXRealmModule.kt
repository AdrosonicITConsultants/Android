package com.adrosonic.craftexchange.database

import com.adrosonic.craftexchange.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.database.entities.realmEntities.brandProducts.BrandList
import com.adrosonic.craftexchange.database.entities.realmEntities.brandProducts.BrandProducts
import io.realm.annotations.RealmModule


@RealmModule(classes = [
    UserAddress::class,
    CraftUser::class,
    PaymentAccount::class,
    ArtisanProducts::class,
    BrandProducts::class,
    BrandList::class,
    CategoryProducts::class,
    ClusterProducts::class,
    ProductDimens::class,
    ArtisanProductCategory::class,
    ClusterList::class
])
class CXRealmModule {
}