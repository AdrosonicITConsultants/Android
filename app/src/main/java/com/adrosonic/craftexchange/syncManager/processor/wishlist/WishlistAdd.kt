package com.adrosonic.craftexchange.syncManager.processor.wishlist

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.services.ProductCreateService
import com.adrosonic.craftexchange.services.wishlist.WishlistAddService
import com.adrosonic.craftexchange.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchange.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType


class WishlistAdd: ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { addToWishlist(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${ProductCatalogue.COLUMN_ACTION_WISHLISTED}=1"

    fun addToWishlist(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(WishlistAddService.KEY_ID,itemType.id) }
        WishlistAddService.enqueueWork(context, work)
    }

}