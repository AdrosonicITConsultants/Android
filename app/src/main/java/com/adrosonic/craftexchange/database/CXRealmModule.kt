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
    Notifications::class,
    Moqs::class,
    PiDetails::class,
    Orders::class,
    EnquiryProductDetails::class,
    Transactions::class,
    ChatUser::class,
    ChatLogUserData::class,
    QcDetails::class,
    ChangeRequests::class,
    Transactions::class,
    TaxInvDetails::class
])
class CXRealmModule