package com.adrosonic.craftexchange.database

import com.adrosonic.craftexchange.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.database.entities.realmEntities.BrandList
import io.realm.annotations.RealmModule


@RealmModule(classes = [
    UserAddress::class,
    CraftUser::class,
    PaymentAccount::class,
    ArtisanProducts::class,
    BrandList::class,
    CategoryProducts::class,
    ProductDimens::class,
    ArtisanProductCategory::class,
    ClusterList::class,
    ProductCatalogue::class,
    ProductImages::class,
    RelatedProducts::class,
    WeaveTypes::class,
    ProductCares::class,
    BuyerCustomProduct::class,
    Enquiries::class,
    OngoingEnquiries::class,
    CompletedEnquiries::class,
    EnquiryPaymentDetails::class,
    Notifications::class
])
class CXRealmModule