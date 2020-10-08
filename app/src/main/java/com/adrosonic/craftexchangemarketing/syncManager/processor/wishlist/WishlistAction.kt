package com.adrosonic.craftexchangemarketing.syncManager.processor.wishlist

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.services.wishlist.WishlistService
import com.adrosonic.craftexchangemarketing.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchangemarketing.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchangemarketing.syncManager.processor.objects.ItemType


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