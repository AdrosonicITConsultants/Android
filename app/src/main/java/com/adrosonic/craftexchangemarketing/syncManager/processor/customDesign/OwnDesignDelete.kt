package com.adrosonic.craftexchangemarketing.syncManager.processor.customDesign

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchangemarketing.services.customDesign.DeleteOwnProductService
import com.adrosonic.craftexchangemarketing.syncManager.processor.ChangeProcessor
import com.adrosonic.craftexchangemarketing.syncManager.processor.InProgressTracker
import com.adrosonic.craftexchangemarketing.syncManager.processor.objects.ItemType

class OwnDesignDelete :
    ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { deleteOwnProduct(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${BuyerCustomProduct.COLUMN_ACTION_DELETED}=1"


    fun deleteOwnProduct(itemType: ItemType, context: Context) {
        val work = Intent().apply { putExtra(DeleteOwnProductService.KEY_ID, itemType.id) }
        DeleteOwnProductService.enqueueWork(context, work)
    }

}