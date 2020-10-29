package com.adrosonic.craftexchange.syncManager.processor.wishlist

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.services.wishlist.WishlistService
import com.adrosonic.craftexchange.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchange.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType


class WishlistAction: ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { actionToWishlist(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${ProductCatalogue.COLUMN_ACTION_WISHLISTED}=1"

    fun actionToWishlist(itemType: ItemType, context: Context){
        var work = Intent().apply { putExtra(WishlistService.KEY_ID,itemType.id) }
        WishlistService.enqueueWork(context, work)
    }

}